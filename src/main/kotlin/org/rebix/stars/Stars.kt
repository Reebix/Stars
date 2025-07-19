package org.rebix.stars

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.player.AttackEntityCallback
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.minecraft.command.argument.ItemStackArgumentType
import net.minecraft.component.ComponentType
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.TooltipDisplayComponent
import net.minecraft.entity.EntityType
import net.minecraft.entity.passive.ChickenEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.particle.ParticleTypes
import net.minecraft.screen.GenericContainerScreenHandler
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.math.Box
import java.util.*
import kotlin.math.min


class Stars : ModInitializer {

    val colors: Vector<Formatting> = Vector()


    init {
        instance = this
        colors.add(Formatting.GOLD)
        colors.add(Formatting.LIGHT_PURPLE)
        colors.add(Formatting.AQUA)
        colors.add(Formatting.RED)
        colors.add(Formatting.GREEN)
        colors.add(Formatting.YELLOW)
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

    override fun onInitialize() {
        // This is where you can initialize your mod. This method is called when Minecraft is starting.
        // You can use this to register commands, events, and other mod-related functionality.
        println("Stars has been initialized!")



        CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
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
        }

        CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
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
        }

        CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
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
        }
        CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
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
        }

        CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
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


        CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
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

                    val sHelmet = SItem("PERFECT_HELMET_13", Items.DIAMOND_HELMET)
                    sHelmet.name = "Perfect Helmet - Tier XIII"
                    sHelmet.rarity = SRarity.LEGENDARY
                    sHelmet.type = SItemType.HELMET
                    sHelmet.reforgeable = true
                    sHelmet.gemstoneSlots.add(SGemstoneSlotType.AMETHYST, true, SGemstoneType.FINE_AMETHYST)
                    sHelmet.gemstoneSlots.add(SGemstoneSlotType.AMETHYST, true, SGemstoneType.FINE_AMETHYST)
                    sHelmet.gemstoneSlots.add(SGemstoneSlotType.AMETHYST, true, SGemstoneType.FINE_AMETHYST)
                    sHelmet.baseStats.add(SStat(SStatType.DEFENSE, 350))

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
                            1
                        })
            )
        }


        /*
                CommandRegistrationCallback.EVENT.register({ dispatcher, registryAccess, environment ->
                    dispatcher.register(
                        CommandManager.literal("test_command").then(
                            CommandManager.argument("value", StringArgumentType.string())
                                .executes { context: CommandContext<ServerCommandSource?>? ->
                                    context!!.getSource()!!
                                        .sendFeedback(
                                            { Text.literal("${context.source?.player?.uuid} Called /test_command") },
                                            false
                                        )
                                    val player = context.source?.player!!

                                    val value = StringArgumentType.getString(context, "value")

                                    player.sendMessage(
                                        Text.literal("You called /test_command with value: $value").formatted(Formatting.GREEN),
                                        false
                                    )
                                    1
                                }
                        ))
                })
*/


        AttackEntityCallback.EVENT.register { player, world, hand, entity, hitResult ->


            ActionResult.PASS
        }



        UseItemCallback.EVENT.register { player, world, hand ->
            if (!world.isClient) {
                val itemStack = player.getStackInHand(hand)
                var sItem = SItem(itemStack)
                if (sItem.id == "HYPERION") {
                    player.sendMessage(Text.literal("Wither Impact!"), false)

                    var serverWorld = world as ServerWorld
                    serverWorld.spawnParticles(
                        ParticleTypes.EXPLOSION,
                        player.x,
                        player.y + 1F,
                        player.z,
                        20,
                        0.0,
                        0.0,
                        0.0,
                        0.01
                    )
                    val allEntities = serverWorld.getOtherEntities(
                        player, Box(
                            player.x - 500.0,
                            player.y - 500.0,
                            player.z - 500.0,
                            player.x + 500.0,
                            player.y + 500.0,
                            player.z + 500.0
                        )
                    )

                    allEntities.forEach {
                        it.damage(serverWorld, player.damageSources.playerAttack(player), 1.0F)
                    }

                }


                if (sItem.id == "EGG") {
                    player.sendMessage(Text.literal("Egg!"), false)

                    var serverWorld = world as ServerWorld

                    val allEntities = serverWorld.getOtherEntities(
                        player, Box(
                            player.x - 50.0,
                            player.y - 50.0,
                            player.z - 50.0,
                            player.x + 50.0,
                            player.y + 50.0,
                            player.z + 50.0
                        )
                    )

                    allEntities.forEach {
                        val chicken: ChickenEntity =
                            EntityType.get("minecraft:chicken").get().create(world, null) as ChickenEntity
                        chicken.setPosition(it.x, it.y, it.z)
                        serverWorld.spawnEntity(chicken)
                    }
                }


            }
            ActionResult.PASS // Event nicht
        }

    }

    companion object {
        private val MOD_ID: String = "stars"
        private var test: Boolean = false
        var instance: Stars? = null

    }
}
