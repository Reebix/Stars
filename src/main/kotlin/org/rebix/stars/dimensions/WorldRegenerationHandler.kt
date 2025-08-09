package org.rebix.stars.dimensions

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKey
import net.minecraft.state.property.Property
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class WorldRegenerationHandler {
    companion object {
        val INSTANCE = WorldRegenerationHandler()
    }

    private data class Pending(
        val worldKey: RegistryKey<World>,
        val pos: BlockPos,
        val state: BlockState,
        var ticks: Int
    )

    data class PosJson(
        val x: Int,
        val y: Int,
        val z: Int
    )

    data class StateJson(
        val block: String,
        val properties: Map<String, String>
    )

    data class BlockJson(
        val pos: PosJson,
        val state: StateJson
    )

    private val pending = mutableListOf<Pending>()

    fun readScanningArrayFromJson(json: String): List<Pair<BlockPos, BlockState>> {
        if (json.isBlank()) return emptyList()
        val gson = Gson()
        val type = object : TypeToken<List<BlockJson>>() {}.type
        val list: List<BlockJson> = gson.fromJson(json, type)
        return list.map { (posJson, stateJson) ->
            var state = Registries.BLOCK.get(Identifier.of(stateJson.block)).defaultState
            stateJson.properties.forEach { (name, value) ->
                @Suppress("UNCHECKED_CAST")
                val prop = state.properties
                    .find { it.name == name } as Property<Comparable<Any>>? ?: return@forEach
                val parsed = prop.parse(value).orElse(null) ?: return@forEach
                state = state.with(prop, parsed)
            }
            BlockPos(posJson.x, posJson.y, posJson.z) to state
        }
    }

    private fun loadJsonFromResources(fileName: String): String {
        return javaClass
            .getResourceAsStream("/$fileName")
            ?.bufferedReader(Charsets.UTF_8)
            ?.use { it.readText() }
            ?: throw IllegalArgumentException("Resource $fileName not found")
    }


    fun addHardcodedBlocks() {
        ModDimensions.HUB_DIMENSION_KEY
        ServerTickEvents.END_SERVER_TICK.register {
            processPendingTasks()
        }

        ServerLifecycleEvents.SERVER_STARTED.register { server ->
            val hub = getAllowedBlocks(ModDimensions.HUB_DIMENSION_KEY)
            val json = loadJsonFromResources("blocks/hub_blocks.json")
            hub.addAll(
                readScanningArrayFromJson(json)
            )
            putAllowedBlocks(ModDimensions.HUB_DIMENSION_KEY, hub)
        }

    }

    val allowedBlocks: MutableMap<RegistryKey<World>, MutableList<Pair<BlockPos, BlockState>>> = mutableMapOf()

    fun putAllowedBlocks(worldKey: RegistryKey<World>, blocks: MutableList<Pair<BlockPos, BlockState>>) {
        allowedBlocks[worldKey] = blocks
    }

    fun getAllowedBlocks(worldKey: RegistryKey<World>): MutableList<Pair<BlockPos, BlockState>> {
        return allowedBlocks.getOrDefault(worldKey, mutableListOf())
    }

    fun isBlockAllowed(worldKey: RegistryKey<World>, position: BlockPos): Boolean {
        return allowedBlocks.getOrDefault(worldKey, mutableListOf()).any { it.first == position }
    }

    private var regenerationEnabled: Boolean = true

    fun isRegenerationEnabled(): Boolean {
        return regenerationEnabled
    }

    fun setRegenerationEnabled(enabled: Boolean) {
        regenerationEnabled = enabled
    }

    fun toggleRegeneration() {
        regenerationEnabled = !regenerationEnabled
    }

    fun hideAllRegenerationBlocks() {
        if (!isRegenerationEnabled()) return
        allowedBlocks.forEach { (worldKey, blocks) ->
            val world = ModDimensions.getWorld(worldKey)
            blocks.forEach { (pos, _) ->
                world.setBlockState(pos, Blocks.AIR.defaultState)
            }
        }
        pending.clear()
    }

    fun regenAll() {
        if (!isRegenerationEnabled()) return
        allowedBlocks.forEach { (worldKey, blocks) ->
            val world = ModDimensions.getWorld(worldKey)
            blocks.forEach { (pos, state) ->
                world.setBlockState(pos, state)
            }
        }
        pending.clear()
    }


    fun enqueueBlockForRegeneration(registryKey: RegistryKey<World>, pos: BlockPos, ticks: Int = 100) {
        if (!isRegenerationEnabled()) return
        val state = allowedBlocks[registryKey]?.find { it.first == pos }?.second ?: return
        pending.add(Pending(registryKey, pos, state, ticks))
    }

    private fun processPendingTasks() {
        val iterator = pending.iterator()
        while (iterator.hasNext()) {
            val task = iterator.next()
            task.ticks--
            if (task.ticks <= 0) {
                val world = ModDimensions.getWorld(task.worldKey)
                world.setBlockState(task.pos, task.state)
                iterator.remove()
            }
        }
    }

}