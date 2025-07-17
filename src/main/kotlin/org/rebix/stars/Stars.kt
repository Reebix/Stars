package org.rebix.stars

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.component.ComponentType
import net.minecraft.component.DataComponentTypes
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

                    var helmet = ItemStack(Items.DIAMOND_HELMET)
                    helmet.set(
                        DataComponentTypes.ITEM_NAME,
                        Text.literal("Perfect Helmet - Tier XIII").formatted(Formatting.GOLD).append(getStarText(10))
                    )
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
                 CommandManager.literal("test_command").then(CommandManager.argument("value", IntegerArgumentType.integer(1, 100))
                     .executes { context: CommandContext<ServerCommandSource?>? ->
                         context!!.getSource()!!
                             .sendFeedback({ Text.literal("${context.source?.player?.uuid} Called /test_command") }, false)
                         val player = context.source?.player!!
                         val amount = IntegerArgumentType.getInteger(context, "value")
                         val sack = ItemStack(Items.CHEST, amount)

                         sack.set(DataComponentTypes.ITEM_NAME, Text.literal("Test Sack"))
                         sack.set(DataComponentTypes.LORE, LoreComponent.DEFAULT.with( Text.literal("This is a lore test!").formatted(
                             Formatting.RED)))
                         sack.apply(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT) { comp ->
                             comp.apply({ currentNbt ->
                                 currentNbt.putInt("key", 0)
                             })
                         }
 //                        player.inventory.insertStack(sack)
                         player.velocity = Vec3d(0.0, amount.toDouble(), 0.0)
                         player.velocityModified = true
                         test = true
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
