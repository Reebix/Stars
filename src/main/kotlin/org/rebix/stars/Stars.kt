package org.rebix.stars

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.command.argument.ItemStackArgumentType
import net.minecraft.component.ComponentType
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.LoreComponent
import net.minecraft.component.type.NbtComponent
import net.minecraft.component.type.TooltipDisplayComponent
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.screen.GenericContainerScreenHandler
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import java.util.*
import kotlin.math.min


class Stars : ModInitializer {

    val colors: Vector<Formatting> = Vector()


    init {
        colors.add(Formatting.GOLD)
        colors.add(Formatting.LIGHT_PURPLE)
        colors.add(Formatting.AQUA)
        colors.add(Formatting.RED)
        colors.add(Formatting.GREEN)
        colors.add(Formatting.YELLOW)
    }

    fun getStarText(amount: Int): Text {
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
                CommandManager.literal("analyze").executes { context: CommandContext<ServerCommandSource?>? ->
                    if (context!!.source?.player == null) {
                        context.getSource()!!.sendFeedback({ Text.literal("You are not a player!") }, false)
                    }
                    val player = context.source?.player!!
                    val itemStack = player.getStackInHand(Hand.MAIN_HAND)
                    val item = SItem(itemStack)
                    player.sendMessage(Text.literal(item.id).append(" ").append(item.name))
                    1
                }
            )
        }
        CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
            dispatcher.register(
                CommandManager.literal("setrarity").then(
                    CommandManager.argument("rarity", StringArgumentType.word())
                        .suggests { _, builder ->
                            for (rarity in SRarity.values()) {
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
                    sHelmet.baseStats.add(SStat(SStatType.DEFENSE, 350))

                    sHelmet.updateItemStack()
                    inv.setStack(4, sHelmet.itemStack)

                    var helmet = ItemStack(Items.DIAMOND_HELMET)
                    helmet.set(
                        DataComponentTypes.ITEM_NAME,
                        Text.literal("Perfect Helmet - Tier XIII").formatted(Formatting.GOLD).append(getStarText(10))
                    )
                    helmet.set(
                        DataComponentTypes.LORE, LoreComponent.DEFAULT.with(
                            Text.literal("Defense: ").styled { it.withItalic(false) }
                                .formatted(Formatting.GRAY).append(Text.literal("+350").formatted(Formatting.GREEN))
                        ).with(
                            Text.literal(" [").styled { it.withItalic(false) }.formatted(Formatting.DARK_GRAY)
                                .append(Text.literal("❈").formatted(Formatting.GRAY))
                                .append(Text.literal("]").formatted(Formatting.DARK_GRAY))
                                .append(Text.literal(" [").formatted(Formatting.DARK_GRAY))
                                .append(Text.literal("❈").formatted(Formatting.GRAY))
                                .append(Text.literal("]").formatted(Formatting.DARK_GRAY))
                                .append(Text.literal(" [").formatted(Formatting.DARK_GRAY))
                                .append(Text.literal("❈").formatted(Formatting.GRAY))
                                .append(Text.literal("]").formatted(Formatting.DARK_GRAY))
                        ).with(Text.literal(""))
                            .with(Text.literal("This item can be reforged!").styled { it.withItalic(false) }
                                .formatted(Formatting.DARK_GRAY))
                            .with(Text.literal("LEGENDARY HELMET").styled { it.withItalic(false) }
                                .formatted(Formatting.GOLD, Formatting.BOLD)))

                    helmet.apply(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT) { comp ->
                        comp.apply({ currentNbt ->
                            currentNbt.putString("id", "PERFECT_HELMET_13")
                        })
                    }
                    inv.setStack(9 + 4, helmet)

                    var chestplate = ItemStack(Items.DIAMOND_CHESTPLATE)
                    chestplate.set(
                        DataComponentTypes.ITEM_NAME,
                        Text.literal("Perfect Chestplate - Tier XIII").formatted(Formatting.GOLD)
                            .append(getStarText(10))
                    )
                    chestplate.apply(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT) { comp ->
                        comp.apply({ currentNbt ->
                            currentNbt.putString("id", "PERFECT_CHESTPLATE_13")
                        })
                    }
                    inv.setStack(9 * 2 + 4, chestplate)
                    var leggings = ItemStack(Items.DIAMOND_LEGGINGS)
                    leggings.set(
                        DataComponentTypes.ITEM_NAME,
                        Text.literal("Perfect Leggings - Tier XIII").formatted(Formatting.GOLD).append(getStarText(10))
                    )
                    leggings.apply(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT) { comp ->
                        comp.apply({ currentNbt ->
                            currentNbt.putString("id", "PERFECT_LEGGINGS_13")
                        })
                    }
                    inv.setStack(9 * 3 + 4, leggings)
                    var boots = ItemStack(Items.DIAMOND_BOOTS)
                    boots.set(
                        DataComponentTypes.ITEM_NAME,
                        Text.literal("Perfect Boots - Tier XIII").formatted(Formatting.GOLD).append(getStarText(10))
                    )
                    boots.apply(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT) { comp ->
                        comp.apply({ currentNbt ->
                            currentNbt.putString("id", "PERFECT_BOOTS_13")
                        })
                    }
                    inv.setStack(9 * 4 + 4, boots)


                    player.openHandledScreen(screenHanderFactory)

                    1
                }.then(
                    CommandManager.argument("amount", IntegerArgumentType.integer(1, colors.size * 5))
                        .executes { context: CommandContext<ServerCommandSource?>? ->

                            if (context!!.source?.player == null) {
                                context.getSource()!!.sendFeedback({ Text.literal("You are not a player!") }, false)
                            }
                            val player = context.source?.player!!

                            val item = player.getStackInHand(Hand.MAIN_HAND)
                            val amount = IntegerArgumentType.getInteger(context, "amount")

                            item.set(
                                DataComponentTypes.CUSTOM_NAME, Text.translatable(item.item.translationKey).append(
                                    getStarText(amount)
                                )
                            )
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

                         UseItemCallback.EVENT.register { player, world, hand ->
                             if (!world.isClient) {
                                 println("${player.name.string} hat mit ${player.getStackInHand(hand)} interagiert?")
                                 player.sendMessage(Text.literal("${player.name.string} hat mit ${Text.translatable(player.getStackInHand(hand).name.string).string} interagiert!"), false)
                             }
                             ActionResult.PASS // Event nicht
                         }
                         UseBlockCallback.EVENT.register { player, world, hand, hitResult ->
                             if (!world.isClient) {
                                 println("${player.name.string} hat mit ${player.getStackInHand(hand)} auf ${hitResult.blockPos} interagiert!")

                             }
                             ActionResult.PASS // Event nicht abbrechen
                         }

                          */
    }

    companion object {
        private val MOD_ID: String = "stars"
        private var test: Boolean = false
    }
}
