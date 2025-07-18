package org.rebix.stars

import net.minecraft.component.ComponentType
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.NbtComponent
import net.minecraft.component.type.TooltipDisplayComponent
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import java.util.*

class SItem(var id: String, var itemType: Item) {

    var itemStack: ItemStack = ItemStack(itemType)
    var name: String = id
    var rarity: SRarity = SRarity.COMMON
    var type = SItemType.NONE
    var reforgeable: Boolean = false
    var baseStats: MutableList<SStat> = mutableListOf()

    constructor(itemStack: ItemStack) : this(id = "NONE", itemType = itemStack.item) {
        this.itemStack = itemStack
        this.itemType = itemStack.item

        itemStack.apply(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT) { comp ->
            comp.apply { currentNbt ->
                this.id = currentNbt.getString("id", "NONE")
                this.name = currentNbt.getString("name", "Unknown Item")
                this.rarity = SRarity.valueOf(currentNbt.getString("rarity", "COMMON").uppercase(Locale.getDefault()))
                this.type = SItemType.valueOf(currentNbt.getString("type", "NONE").uppercase(Locale.getDefault()))
                this.reforgeable = currentNbt.getBoolean("reforgeable", false)
                val statsString = currentNbt.getString("baseStats", "[]")
                // Parse baseStats from string representation
                if (statsString.isNotEmpty()) {
                    try {
                        val statsList = statsString.removeSurrounding("[", "]").split(",")
                        statsList.forEach { stat ->
                            val parts = stat.split(":")
                            if (parts.size == 2) {
                                val type = SStatType.valueOf(parts[0].trim().uppercase(Locale.getDefault()))
                                val value = parts[1].trim().toDoubleOrNull() ?: 0.0
                                baseStats.add(SStat(type, value))
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }


    fun updateItemStack() {
        itemStack.withItem { itemType }

        // Lore Builder
        val loreBuilder = LoreBuilder()
        baseStats.forEach { stat ->
            loreBuilder.addLine(
                Text.literal(stat.type.displayName + ": ")
                    .formatted(Formatting.GRAY)
                    .append(
                        Text.literal("${if (stat.value >= 0) "+" else "-"}${stat.formattedValue}")
                            .formatted(stat.type.formatting)
                    )
            )
        }
        loreBuilder.addLine()
        if (reforgeable) {
            loreBuilder.addLine(Text.literal("This item can be reforged!").formatted(Formatting.DARK_GRAY))
        }
        loreBuilder.addLine(rarity.getText().append(" ").append(type.toString())).build()


        itemStack.set(DataComponentTypes.LORE, loreBuilder.build())
        itemStack.set(DataComponentTypes.ITEM_NAME, Text.literal(name).formatted(rarity.formatting))

        // Custom Data Component
        itemStack.apply(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT) { comp ->
            comp.apply({ currentNbt ->
                currentNbt.putString("id", id)
                currentNbt.putString("name", name)
                currentNbt.putString("rarity", rarity.name)
                currentNbt.putString("type", type.name)
                currentNbt.putBoolean("reforgeable", reforgeable)
                currentNbt.putString("baseStats", baseStats.toString())
            })
        }

        // Hide tooltip display
        val emptySequencedSet: SequencedSet<ComponentType<*>> = LinkedHashSet()
        emptySequencedSet.add(DataComponentTypes.ATTRIBUTE_MODIFIERS)
        val tooltipDisplayComponent = TooltipDisplayComponent(false, emptySequencedSet)
        itemStack.set(DataComponentTypes.TOOLTIP_DISPLAY, tooltipDisplayComponent)
    }
}
