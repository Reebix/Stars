package org.rebix.stars.dimensions

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.ActionResult
import net.minecraft.util.Identifier
import net.minecraft.world.GameRules
import org.rebix.stars.Stars
import org.rebix.stars.dimensions.DimensionTags.NO_GROWTH

class ModDimensions {
    companion object {
        val HUB_DIMENSION_KEY = RegistryKey.of(RegistryKeys.WORLD, Identifier.of(Stars.MOD_ID, "hub"))!!
        val HUB_TYPE_KEY = RegistryKey.of(RegistryKeys.DIMENSION_TYPE, Identifier.of(Stars.MOD_ID, "hub"))!!
        val HUB_DIMENSION_TAGS =
            setOf(
                NO_GROWTH, DimensionTags.NO_PORTAL,
                DimensionTags.NO_BREAKING, DimensionTags.NO_INTERACTING, DimensionTags.NO_FLUIDS, DimensionTags.NO_FIRE,
            )


        val NETHER_DIMENSION_KEY = RegistryKey.of(RegistryKeys.WORLD, Identifier.of(Stars.MOD_ID, "nether"))!!
        val NETHER_TYPE_KEY =
            RegistryKey.of(RegistryKeys.DIMENSION_TYPE, Identifier.of(Stars.MOD_ID, "nether"))!!
        val NETHER_DIMENSION_TAGS =
            setOf(
                NO_GROWTH, DimensionTags.NO_PORTAL,
                DimensionTags.NO_BREAKING, DimensionTags.NO_INTERACTING, DimensionTags.NO_FLUIDS, DimensionTags.NO_FIRE,
            )

        val DIMENSION_DICT = mapOf(
            HUB_DIMENSION_KEY to HUB_DIMENSION_TAGS,
            NETHER_DIMENSION_KEY to NETHER_DIMENSION_TAGS
        )

        fun register() {
            // Prevent breaking
            AttackBlockCallback.EVENT.register { player, world, hand, pos, direction ->
                var cancel = false
                if (DIMENSION_DICT[world.registryKey]?.contains(DimensionTags.NO_BREAKING) == true) {
                    // Prevent breaking blocks in the hub dimension
                    cancel = true
                }

                if (cancel) ActionResult.FAIL else ActionResult.PASS
            }

            // Prevent interacting with blocks
            UseBlockCallback.EVENT.register { player, world, hand, hitResult ->
                var cancel = false
                if (DIMENSION_DICT[world.registryKey]?.contains(DimensionTags.NO_INTERACTING) == true) {
                    // Prevent interacting with blocks in the hub dimension
                    cancel = true
                }

                if (cancel) ActionResult.FAIL else ActionResult.PASS
            }

            // Set Game Rules for dimensions
            ServerLifecycleEvents.SERVER_STARTED.register { server ->
                DIMENSION_DICT.forEach { (dimensionKey, tags) ->
                    val dimension = server.getWorld(dimensionKey)
                    if (dimension != null) {
                        if (tags.contains(DimensionTags.NO_FIRE)) {
                            dimension.gameRules.get(GameRules.DO_FIRE_TICK)
                                .set(false, server)
                        }

                    }
                }
            }

        }
    }
}