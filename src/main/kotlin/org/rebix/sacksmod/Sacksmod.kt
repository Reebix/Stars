package org.rebix.sacksmod

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.LoreComponent
import net.minecraft.component.type.NbtComponent
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Formatting
import net.minecraft.util.math.Vec3d
import java.util.Vector


class Sacksmod : ModInitializer {

    override fun onInitialize() {
        // This is where you can initialize your mod. This method is called when Minecraft is starting.
        // You can use this to register commands, events, and other mod-related functionality.
        println("Sacksmod has been initialized!")

        CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
            // Register a command that can be used in the game
            dispatcher.register(
                CommandManager.literal("sack")
                    .executes { context: CommandContext<ServerCommandSource?>? ->
                        context!!.getSource()!!
                            .sendFeedback({ Text.literal("Hi Wasuuup\nTest is currently $test") }, false)
                        1
                    }
            )
        }

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
    }

    companion object {
        private val MOD_ID: String = "sacksmod"
        private var test: Boolean = false
    }
}
