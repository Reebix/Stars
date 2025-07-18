package org.rebix.stars

import net.minecraft.component.ComponentType
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.NbtComponent
import net.minecraft.component.type.TooltipDisplayComponent
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.MutableText
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
    var stars: Int = 0
    var recombobulated: Boolean = false
    var gemstoneSlots: MutableList<SGemstoneSlot> = mutableListOf()
    var reforge: SReforge = SReforge.NONE

    constructor(itemStack: ItemStack) : this(id = "NONE", itemType = itemStack.item) {
        //TODO: create lookup for itemStack

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
                        val statsList = statsString.removeSurrounding("[", "]").replace(" ", "").split(",")
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
                this.stars = currentNbt.getInt("stars", 0)
                this.recombobulated = currentNbt.getBoolean("recombobulated", false)
                this.reforge = SReforge.valueOf(currentNbt.getString("reforge", "NONE").uppercase(Locale.getDefault()))
            }
        }
    }


    fun updateItemStack() {
        itemStack.withItem { itemType }

        var effectiveRarity = rarity
        if (recombobulated) {
            // If recombobulated, increase the rarity by one level
            effectiveRarity = if (rarity.ordinal < SRarity.entries.size - 2) {
                SRarity.entries[rarity.ordinal + 1]
            } else {
                rarity // Keep the same if already at max rarity or would be admin
            }
        }
        val isDungeon = baseStats.any { it.type == SStatType.GEAR_SCORE }
        val highestRarity = reforge.bonuses.keys.maxOrNull()

        // Calculate stats
        val stats: MutableList<SStat> =
            MutableList(baseStats.size) { index -> SStat(baseStats[index].type, baseStats[index].value) }
        if (reforge != SReforge.NONE) {
            // Apply reforge bonuses
            val bonuses = reforge.bonuses.getOrDefault(effectiveRarity, reforge.bonuses[highestRarity])!!
            bonuses.forEach { bonusStat ->
                val existingStat = stats.find { it.type == bonusStat.type }
                if (existingStat != null) {
                    existingStat.value += bonusStat.value
                } else {
                    stats.add(SStat(bonusStat.type, bonusStat.value))
                }
            }

        }

        fun statText(stat: SStat): MutableText {
            return Text.literal("${if (stat.type.display == SStatTypeDisplay.BASE || stat.type.display == SStatTypeDisplay.PERCENTAGE) if (stat.value >= 0) "+" else "-" else ""}${stat.formattedValue}${if (stat.type.display == SStatTypeDisplay.PERCENTAGE) "%" else ""}")
        }

        // Lore Builder
        val loreBuilder = LoreBuilder()
        stats.sortBy { it.type.ordinal } // Sort by type ordinal for consistent order
        stats.forEach { stat ->
            println(
                reforge.bonuses.getOrDefault(effectiveRarity, reforge.bonuses[highestRarity])!!
                    .find { it.type == stat.type }
            )
            val reforgeStat = reforge.bonuses.getOrDefault(effectiveRarity, reforge.bonuses[highestRarity])!!
                .find { it.type == stat.type }
            val reforgeText = if (reforgeStat != null) {
                Text.literal(" (${statText(stat).string})")
                    .formatted(Formatting.BLUE)
            } else {
                Text.literal("")
            }

            loreBuilder.addLine(
                Text.literal(stat.type.displayName + ": ")
                    .formatted(Formatting.GRAY)
                    .append(statText(stat).formatted(stat.type.formatting)).append(reforgeText)
            )

        }
        loreBuilder.addLine()
        if (reforgeable && reforge == SReforge.NONE) {
            loreBuilder.addLine(Text.literal("This item can be reforged!").formatted(Formatting.DARK_GRAY))
        }

        val dungeonText = Text.literal(if (isDungeon) "DUNGEON " else "")
        if (recombobulated) {

            var recomtext = Text.literal("K").formatted(Formatting.OBFUSCATED).formatted(effectiveRarity.formatting)
                .formatted(Formatting.BOLD)

            loreBuilder.addLine(
                recomtext.copy().append(
                    Text.literal(" ")
                        .append(
                            effectiveRarity.getText().append(" ").append(dungeonText).append(type.toString())
                                .styled { style ->
                                    style.withObfuscated(false)
                                })
                        .append(" ")
                ).append(recomtext.copy())
            )
        } else loreBuilder.addLine(effectiveRarity.getText().append(" ").append(dungeonText).append(type.toString()))
        // Lore builder ends here

        var reforgeText = Text.literal(if (reforge != SReforge.NONE) reforge.displayName + " " else "")

        itemStack.set(DataComponentTypes.LORE, loreBuilder.build())
        itemStack.set(
            DataComponentTypes.ITEM_NAME,
            reforgeText.append(
                Text.literal(name)
            ).formatted(effectiveRarity.formatting).append(Stars.instance!!.getStarText(stars))

        )

        // Custom Data Component
        itemStack.apply(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT) { comp ->
            comp.apply({ currentNbt ->
                currentNbt.putString("id", id)
                currentNbt.putString("name", name)
                currentNbt.putString("rarity", rarity.name)
                currentNbt.putString("type", type.name)
                currentNbt.putBoolean("reforgeable", reforgeable)
                currentNbt.putString("baseStats", baseStats.toString())
                currentNbt.putInt("stars", stars)
                currentNbt.putBoolean("recombobulated", recombobulated)
                currentNbt.putString("reforge", reforge.name)
            })
        }

        // Hide tooltip display
        val emptySequencedSet: SequencedSet<ComponentType<*>> = LinkedHashSet()
        emptySequencedSet.add(DataComponentTypes.ATTRIBUTE_MODIFIERS)
        val tooltipDisplayComponent = TooltipDisplayComponent(false, emptySequencedSet)
        itemStack.set(DataComponentTypes.TOOLTIP_DISPLAY, tooltipDisplayComponent)
    }


}
