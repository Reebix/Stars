package org.rebix.stars

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.minecraft.block.BlockState
import net.minecraft.command.argument.ItemStackArgumentType
import net.minecraft.component.ComponentType
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.TooltipDisplayComponent
import net.minecraft.entity.EntityType
import net.minecraft.entity.decoration.DisplayEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.screen.GenericContainerScreenHandler
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import org.rebix.stars.Stars.Companion.inventoryMap
import org.rebix.stars.combat.SCombatEntity
import org.rebix.stars.combat.entity.SEntityType
import org.rebix.stars.dimensions.ModDimensions
import org.rebix.stars.dimensions.WorldRegenerationHandler
import org.rebix.stars.item.*
import org.rebix.stars.stats.SStat
import org.rebix.stars.stats.SStatType
import java.util.*
import kotlin.math.min

class CommandRegistry {
    val colors: Vector<Formatting> = Vector()

    init {
        colors.add(Formatting.GOLD)
        colors.add(Formatting.LIGHT_PURPLE)
        colors.add(Formatting.AQUA)
        colors.add(Formatting.RED)
        colors.add(Formatting.GREEN)
        colors.add(Formatting.YELLOW)
    }

    companion object {
        val INSTANCE = CommandRegistry()
    }

    var currentlyScanning = false
    var scanningArray: MutableList<Pair<BlockPos, BlockState>> = mutableListOf()

    /**
     * Registers all commands for the mod.
     */
    fun registerCommands() {
        CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
            dispatcher.register(
                CommandManager.literal("regenall").executes { context: CommandContext<ServerCommandSource?>? ->
                    WorldRegenerationHandler.INSTANCE.regenAll()
                    1
                }
            )

            dispatcher.register(
                CommandManager.literal("hideall").executes { context: CommandContext<ServerCommandSource?>? ->
                    WorldRegenerationHandler.INSTANCE.hideAllRegenerationBlocks()
                    1
                }
            )

            dispatcher.register(
                CommandManager.literal("scanblocks").executes { context: CommandContext<ServerCommandSource?>? ->
                    if (context!!.source?.player == null) {
                        context.getSource()!!.sendFeedback({ Text.literal("You are not a player!") }, false)
                    }
                    val player = context.source?.player!!

                    if (currentlyScanning) {
                        currentlyScanning = false
                        player.sendMessage(Text.literal("Stars mod finished scanning!"))

                        // in CommandRegistry.kt

                        fun valueToJson(value: Comparable<*>): String =
                            when (value) {
                                is Boolean, is Number -> value.toString()
                                is Direction.Axis -> "\"${value.name.lowercase()}\""
                                is Enum<*> -> "\"${value.name.lowercase()}\""
                                else -> "\"$value\""
                            }

                        fun posToJson(pos: BlockPos): String =
                            """{"x":${pos.x},"y":${pos.y},"z":${pos.z}}"""

                        fun stateToJson(state: BlockState): String {
                            val id = Registries.BLOCK.getId(state.block).toString()
                            val sb = StringBuilder()
                            sb.append("{\"block\":\"").append(id).append("\",\"properties\":{")
                            var first = true
                            for (entry in state.entries) {
                                if (!first) sb.append(",")
                                first = false
                                val prop = entry.key
                                val value = entry.value
                                sb.append("\"").append(prop.name).append("\":").append(valueToJson(value))
                            }
                            sb.append("}}")
                            return sb.toString()
                        }

                        fun toJsonArray(scanningArray: List<Pair<BlockPos, BlockState>>): String =
                            buildString {
                                append("[")
                                scanningArray.forEachIndexed { index, (pos, state) ->
                                    append("""{"pos":${posToJson(pos)},"state":${stateToJson(state)}}""")
                                    if (index < scanningArray.lastIndex) append(",")
                                }
                                append("]")
                            }

                        // Nutzung nach dem Scannen:
                        val jsonOutput = toJsonArray(scanningArray)
                        println(jsonOutput)

                        /*
                        fun formatBlockPos(pos: BlockPos): String =
                            "BlockPos(${pos.x}, ${pos.y}, ${pos.z})"

                        fun formatValue(prop: Property<*>, value: Comparable<*>): String =
                            when (value) {
                                is Boolean, is Number -> value.toString()

                                is Direction.Axis -> "Direction.Axis.${value.name}"

                                is Enum<*> ->
                                    // z.B. Direction.Axis.X
                                    "${value::class.simpleName}.${value.name}"

                                else -> "\"$value\""
                            }
                        Pair(
                            BlockPos(-114, 74, -28),
                            Blocks.OAK_LOG.defaultState.with(Properties.AXIS, Direction.Axis.Y)
                        )
                        fun formatBlockState(state: BlockState): String =
                            buildString {
                                val id = Registries.BLOCK.getId(state.block).path.uppercase()
                                append("Blocks.$id.defaultState")
                                state.entries.forEach { (prop, value) ->
                                    append(".with(Properties.${prop.name.uppercase()}, ${formatValue(prop, value)})")
                                }
                            }

                        println("Scanning Array:\n")
                        print("listOf(")
                        scanningArray.forEach { (pos, state) ->
                            print("    Pair(${formatBlockPos(pos)}, ${formatBlockState(state)}),")
                        }

                         */
                        print("\n\n")


//                        println(scanningArray)
//                        println(
//                            scanningArray.toString().replace("(", "Vec3d(").replace("[", "listOf(").replace("]", ")")
//                        )
                        return@executes 1
                    }
                    currentlyScanning = true
                    player.sendMessage(Text.literal("Stars mod is now scanning!"))
                    1
                }
            )
            dispatcher.register(
                CommandManager.literal("dummy").executes { context: CommandContext<ServerCommandSource?>? ->
                    val player = context!!.source?.player!!

                    for (i in 0 until 100) {
                        val dummy = SCombatEntity(
                            SEntityType.BLAZE,
                            player.world,
                            Text.literal("Dummy").formatted(Formatting.GOLD),
                            position = player.pos
                        )
                        dummy._maxHealth = 100_000
                        dummy.health = 100_000
                        dummy.updateHealthText()
                    }
                    1
                }
            )


            // Register a command that can be used in the game
            dispatcher.register(
                CommandManager.literal("star").executes { context: CommandContext<ServerCommandSource?>? ->
                    if (context!!.source?.player == null) {
                        context.getSource()!!.sendFeedback({ Text.literal("You are not a player!") }, false)
                    }
                    val player = context.source?.player!!


                    var inv = SimpleInventory(9 * 6)
                    val screenHanderFactory = object : NamedScreenHandlerFactory {
                        override fun createMenu(
                            syncId: Int,
                            playerInventory: PlayerInventory,
                            player: PlayerEntity
                        ): ScreenHandler {
                            inventoryMap[syncId] = inv
                            return GenericContainerScreenHandler.createGeneric9x6(
                                syncId,
                                playerInventory,
                                inv
                            )
                        }


                        override fun getDisplayName(): Text = Text.literal("Star Inventory").append(getStarText(11))

                    }


                    val emptySequencedSet: SequencedSet<ComponentType<*>> = LinkedHashSet()
                    var component = TooltipDisplayComponent(true, emptySequencedSet)
                    val item = ItemStack(Items.BLACK_STAINED_GLASS_PANE)
                    item.set(DataComponentTypes.ITEM_NAME, Text.literal(" "))
                    item.set(DataComponentTypes.TOOLTIP_DISPLAY, component)

                    for (i in 0 until inv.size()) {
                        inv.setStack(i, item.copy())
                    }

                    val necronHandle = SItem("NECRON_HANDLE", Items.STICK)
                    necronHandle.name = "Necron's Handle"
                    necronHandle.rarity = SRarity.LEGENDARY
                    necronHandle.type = SItemType.ITEM
                    necronHandle.isDungeon = true
                    necronHandle.baseStats.add(SStat(SStatType.DAMAGE, 100000))
                    necronHandle.baseStats.add(SStat(SStatType.STRENGTH, 1000))

                    necronHandle.updateItemStack()
                    inv.setStack(9 * 1 + 4, necronHandle.itemStack)

                    val treeCapitator = SItem("TREECAPITATOR_AXE", Items.GOLDEN_AXE)
                    treeCapitator.name = "Tree Capitator"
                    treeCapitator.rarity = SRarity.EPIC
                    treeCapitator.type = SItemType.AXE
                    treeCapitator.baseStats.add(SStatType.SWEEP, 25)
                    treeCapitator.gemstoneSlots.add(SGemstoneSlotType.CITRINE, false)
                    treeCapitator.gemstoneSlots.add(SGemstoneSlotType.CITRINE, false)

                    treeCapitator.updateItemStack()
                    inv.setStack(1, treeCapitator.itemStack)

                    val sHelmet = SItem("PERFECT_HELMET_13", Items.DIAMOND_HELMET)
                    sHelmet.name = "Perfect Helmet - Tier XIII"
                    sHelmet.rarity = SRarity.LEGENDARY
                    sHelmet.type = SItemType.HELMET
                    sHelmet.reforgeable = true
                    sHelmet.gemstoneSlots.add(SGemstoneSlotType.AMETHYST, true, SGemstoneType.FINE_AMETHYST)
                    sHelmet.gemstoneSlots.add(SGemstoneSlotType.AMETHYST, true, SGemstoneType.FINE_AMETHYST)
                    sHelmet.gemstoneSlots.add(SGemstoneSlotType.AMETHYST, true, SGemstoneType.FINE_AMETHYST)
                    sHelmet.baseStats.add(SStat(SStatType.DEFENSE, 350))
                    sHelmet.baseStats.add(SStatType.DAMAGE, 10000)
                    sHelmet.baseStats.add(SStatType.STRENGTH, 10000)
                    sHelmet.baseStats.add(SStatType.CRIT_CHANCE, 10000)
                    sHelmet.baseStats.add(SStatType.CRIT_DAMAGE, 10000)

                    val terminator = SItem("TERMINATOR", Items.BOW)
                    terminator.name = "Terminator"
                    terminator.rarity = SRarity.LEGENDARY
                    terminator.type = SItemType.BOW
                    terminator.baseStats.add(SStatType.DAMAGE, 310000000)
                    terminator.baseStats.add(SStatType.STRENGTH, 50)
                    terminator.baseStats.add(SStatType.CRIT_DAMAGE, 250)
                    terminator.baseStats.add(SStatType.BONUS_ATTACK_SPEED, 40)
                    terminator.baseStats.add(SStatType.SHOT_COOLDOWN, 0.5)
                    terminator.isShortbow = true
                    terminator.reforgeable = true

                    terminator.updateItemStack()
                    inv.setStack(0, terminator.itemStack)

                    sHelmet.updateItemStack()
                    inv.setStack(4, sHelmet.itemStack)

                    val lividDagger = SItem("LIVID_DAGGER", Items.IRON_SWORD)
                    lividDagger.name = "Livid Dagger"
                    lividDagger.rarity = SRarity.LEGENDARY
                    lividDagger.baseStats.add(SStatType.GEAR_SCORE, 1039)
                    lividDagger.baseStats.add(SStatType.DAMAGE, 210)
                    lividDagger.baseStats.add(SStatType.STRENGTH, 60)
                    lividDagger.baseStats.add(SStatType.CRIT_CHANCE, 100)
                    lividDagger.baseStats.add(SStatType.CRIT_DAMAGE, 50)
                    lividDagger.baseStats.add(SStatType.BONUS_ATTACK_SPEED, 50)
                    lividDagger.type = SItemType.SWORD
                    lividDagger.reforgeable = true
                    lividDagger.reforge = SReforge.DIRTY
                    lividDagger.stars = 5
                    lividDagger.hotPotatoBooks = 10
                    lividDagger.fumingPotatoBooks = 5
                    lividDagger.isDungeon = true
                    lividDagger.gemstoneSlots.add(SGemstoneSlotType.JASPER, true, SGemstoneType.FINE_JASPER)

                    lividDagger.updateItemStack()
                    inv.setStack(9 * 2 + 4, lividDagger.itemStack)

                    var hype = SItem("HYPERION", Items.IRON_SWORD)
                    hype.name = "Hyperion"
                    hype.rarity = SRarity.LEGENDARY
                    hype.type = SItemType.SWORD
                    hype.reforgeable = true
                    hype.baseStats.add(SStat(SStatType.DAMAGE, 260))
                    hype.baseStats.add(SStat(SStatType.GEAR_SCORE, 715))
                    hype.baseStats.add(SStat(SStatType.STRENGTH, 150))
                    hype.baseStats.add(SStat(SStatType.INTELLIGENCE, 350))
                    hype.baseStats.add(SStat(SStatType.FEROCITY, 30))
                    hype.reforge = SReforge.HEROIC
                    hype.gemstoneSlots.add(SGemstoneSlotType.SAPPHIRE, true, SGemstoneType.PERFECT_SAPPHIRE)
                    hype.gemstoneSlots.add(SGemstoneSlotType.SAPPHIRE, true, SGemstoneType.PERFECT_SAPPHIRE)
                    lividDagger.isDungeon = true

                    hype.updateItemStack()
                    inv.setStack(9 * 5 + 4, hype.itemStack)

                    player.openHandledScreen(screenHanderFactory)

                    1
                }.then(
                    CommandManager.argument("amount", IntegerArgumentType.integer(0, colors.size * 5))
                        .executes { context: CommandContext<ServerCommandSource?>? ->

                            if (context!!.source?.player == null) {
                                context.getSource()!!.sendFeedback({ Text.literal("You are not a player!") }, false)
                            }
                            val player = context.source?.player!!

                            val item = player.getStackInHand(Hand.MAIN_HAND)
                            val sItem = SItem(item)
                            sItem.stars = IntegerArgumentType.getInteger(context, "amount")
                            sItem.updateItemStack()
                            //[434] ♫ [VIP+] Rebbix is holding [Pitchin' Hellfire Rod ✪✪✪✪✪]
                            ///give @s minecraft:white_dye[minecraft:item_model="animated_java:blueprint/blueprint/base"]
                            /*

                             */
                            1
                        })
            )


            dispatcher.register(
                CommandManager.literal("removetagged").executes { context: CommandContext<ServerCommandSource?>? ->
                    context!!.source!!.world.iterateEntities().forEach { entity ->
                        if (entity.commandTags.contains("REMOVE")) {
                            entity.kill(context.source!!.world)
                        }
                    }
                    1
                }
            )




            dispatcher.register(
                CommandManager.literal("testing").executes { context: CommandContext<ServerCommandSource?>? ->
                    if (context!!.source?.player == null) {
                        context.getSource()!!.sendFeedback({ Text.literal("You are not a player!") }, false)
                    }
                    val player = context.source?.player!!
                    val healthTextEntity =
                        DisplayEntity.TextDisplayEntity(EntityType.TEXT_DISPLAY, player.world).apply {
                            this.updatePosition(player.x, player.y + 2.0, player.z)
                            this.text =
                                Text.literal(100.toString()).append(Text.literal(" ❤").formatted(Formatting.RED))
                            this.billboardMode = DisplayEntity.BillboardMode.CENTER
                            this.addCommandTag("REMOVE")
                        }
                    player.world.spawnEntity(healthTextEntity)
                    healthTextEntity.startRiding(player, true)
                    1
                }
            )

            dispatcher.register(
                CommandManager.literal("nether").executes { context: CommandContext<ServerCommandSource?>? ->
                    if (context!!.source?.player == null) {
                        context.getSource()!!.sendFeedback({ Text.literal("You are not a player!") }, false)
                    }
                    val player = context.source?.player!!
                    player.teleport(
                        player.server!!.getWorld(ModDimensions.NETHER_DIMENSION_KEY)!!,
                        -360.50, 80.00, -430.50, setOf(), 180.0f, 0.0f, false
                    )
                    1
                }
            )

            dispatcher.register(
                CommandManager.literal("hub").executes { context: CommandContext<ServerCommandSource?>? ->
                    if (context!!.source?.player == null) {
                        context.getSource()!!.sendFeedback({ Text.literal("You are not a player!") }, false)
                    }
                    val player = context.source?.player!!
                    player.teleport(
                        player.server!!.getWorld(ModDimensions.HUB_DIMENSION_KEY)!!,
                        -2.50, 70.00, -69.50, setOf(), 180.0f, 0.0f, false
                    )
                    1
                }
            )

            dispatcher.register(
                CommandManager.literal("updateitem").executes { context: CommandContext<ServerCommandSource?>? ->
                    if (context!!.source?.player == null) {
                        context.getSource()!!.sendFeedback({ Text.literal("You are not a player!") }, false)
                    }
                    val player = context.source?.player!!
                    val itemStack = player.getStackInHand(Hand.MAIN_HAND)
                    val item = SItem(itemStack)
                    item.updateItemStack()
                    1
                }
            )

            dispatcher.register(
                CommandManager.literal("recombobulate").executes { context: CommandContext<ServerCommandSource?>? ->
                    if (context!!.source?.player == null) {
                        context.getSource()!!.sendFeedback({ Text.literal("You are not a player!") }, false)
                    }
                    val player = context.source?.player!!
                    val itemStack = player.getStackInHand(Hand.MAIN_HAND)
                    val item = SItem(itemStack)
                    item.recombobulated = true
                    item.updateItemStack()
                    1
                }
            )

            dispatcher.register(
                CommandManager.literal("analyze").executes { context: CommandContext<ServerCommandSource?>? ->
                    if (context!!.source?.player == null) {
                        context.getSource()!!.sendFeedback({ Text.literal("You are not a player!") }, false)
                    }
                    val player = context.source?.player!!
                    val itemStack = player.getStackInHand(Hand.MAIN_HAND)
                    val item = SItem(itemStack)
                    player.sendMessage(Text.literal(item.id).append(item.name))
                    1
                }
            )

            dispatcher.register(
                CommandManager.literal("setrarity").then(
                    CommandManager.argument("rarity", StringArgumentType.word())
                        .suggests { _, builder ->
                            for (rarity in SRarity.entries) {
                                builder.suggest(rarity.name.lowercase())
                            }
                            builder.buildFuture()
                        }.executes { context: CommandContext<ServerCommandSource?>? ->
                            if (context!!.source?.player == null) {
                                context.getSource()!!.sendFeedback({ Text.literal("You are not a player!") }, false)
                            }
                            val player = context.source?.player!!
                            val itemStack = player.getStackInHand(Hand.MAIN_HAND)
                            val item = SItem(itemStack)
                            val rarityName = StringArgumentType.getString(context, "rarity").uppercase()
                            val rarity = SRarity.valueOf(rarityName)
                            item.rarity = rarity
                            item.updateItemStack()
                            1
                        }
                ))
            dispatcher.register(
                CommandManager.literal("giveitem").then(
                    CommandManager.argument("id", StringArgumentType.string()).then(
                        CommandManager.argument(
                            "item",
                            ItemStackArgumentType.itemStack(registryAccess)
                        ).executes { context: CommandContext<ServerCommandSource?>? ->
                            if (context!!.source?.player == null) {
                                context.getSource()!!.sendFeedback({ Text.literal("You are not a player!") }, false)
                            }
                            val player = context.source?.player!!
                            val id = StringArgumentType.getString(context, "id")
                            val itemStack = ItemStackArgumentType.getItemStackArgument(context, "item").item
                            val item = SItem(id, itemStack)
                            item.updateItemStack()
                            player.inventory.offerOrDrop(item.itemStack)
                            1
                        }
                    )))
        }

        AttackBlockCallback.EVENT.register { player, world, hand, blockPos, dir ->
            fun shpawnHighlight(
                player: PlayerEntity,
                pos: BlockPos,
                state: BlockState
            ) {
                val highlight = DisplayEntity.BlockDisplayEntity(
                    EntityType.BLOCK_DISPLAY,
                    player.world
                )
                highlight.updatePosition(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
                highlight.blockState = state

                // Glow-Effekt aktivieren
                highlight.isGlowing = true

                // Entität spawnen
                player.world.spawnEntity(highlight)
            }
            if (currentlyScanning) {
                if (!player.isSneaking) {

                    if (!scanningArray.any { it.first == blockPos }) {
                        scanningArray.add(Pair(blockPos, world.getBlockState(blockPos)))
                        shpawnHighlight(player, blockPos, world.getBlockState(blockPos))
                        player.sendMessage(Text.literal("Added block at $blockPos to scanning array!"), false)
                    }
                } else {

                    // Innerhalb von UseBlockCallback.EVENT.register
                    val targetState = world.getBlockState(blockPos)
                    val visited = mutableSetOf<BlockPos>()

                    blockPos

                    fun recursiveAdd(pos: BlockPos) {
                        if (pos in visited) return

                        // Nur gleiche Blockarten hinzufügen
                        if (world.getBlockState(pos).block != targetState.block) return

                        visited += pos
                        scanningArray += pos to targetState
                        shpawnHighlight(player, pos, targetState)

                        // Nachbarn in allen 6 Richtungen prüfen
                        for (dx in -1..1) {
                            for (dy in -1..1) {
                                for (dz in -1..1) {
                                    if (dx == 0 && dy == 0 && dz == 0) continue
                                    recursiveAdd(pos.add(dx, dy, dz))
                                }
                            }
                        }
                    }

                    recursiveAdd(blockPos)
                    player.sendMessage(
                        Text.literal("Added connected blocks at $blockPos to scanning array! Total: ${visited.size}"),
                        false
                    )
                    visited.forEach { blockP ->
                        if (!scanningArray.any { it.first == blockP }) {
                            scanningArray.add(Pair(blockP, targetState))
                        }
                    }

                }

            }
            ActionResult.PASS
        }


    }

    fun getStarText(amount: Int): Text {
        if (amount == 0) return Text.empty()
        val remainder = amount % 5
//        println(Integer.parseInt((amount / 5).toString()))

        val starText = Text.literal(" ")
        for (i in 1..min(5, amount)) {
            var color: Formatting?
            if (i <= remainder) {
                color = colors[amount / 5]
            } else {
                color = colors[amount / 5 - 1]
            }
            starText.append(Text.literal("✪").formatted(color))
        }

        return starText
    }
}

