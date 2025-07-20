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
    var baseStats = SStatManager()
    var stars: Int = 0
    var recombobulated: Boolean = false
    var gemstoneSlots = GemstoneManager()
    var reforge: SReforge = SReforge.NONE
    var hotPotatoBooks = 0
    var fumingPotatoBooks = 0
    var isDungeon = false
    var hasRecipe = false

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
                // Parse baseStats from string representation
                val statsString = currentNbt.getString("baseStats", "[]")
                if (statsString.isNotEmpty()) {
                    try {
                        val statsList = statsString.removeSurrounding("[", "]").replace(" ", "").split(",")
                        statsList.forEach { stat ->
                            val parts = stat.split(":")
                            if (parts.size == 2) {
                                val type = SStatType.valueOf(parts[0].trim().uppercase(Locale.getDefault()))
                                val value = parts[1].trim().toDoubleOrNull() ?: 0.0
                                baseStats.add(type, value)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                // Parse gemstone slots
                val gemstoneSlotsString = currentNbt.getString("gemstoneSlots", "[]")
                if (gemstoneSlotsString.isNotEmpty()) {
                    try {
                        val slotsList = gemstoneSlotsString.removeSurrounding("[", "]").replace(" ", "").split(",")
                        slotsList.forEach { slot ->
                            val parts = slot.split(";")
                            if (parts.size == 3) {
                                val type = SGemstoneSlotType.valueOf(
                                    parts[0].split("=")[1].trim().uppercase(Locale.getDefault())
                                )
                                val gemstoneType =
                                    SGemstoneType.valueOf(parts[1].split("=")[1].trim().uppercase(Locale.getDefault()))
                                val unlocked = parts[2].split("=")[1].trim().removeSuffix(")").toBoolean()
                                gemstoneSlots.add(SGemstoneSlot(type).apply {
                                    this.gemstone = gemstoneType
                                    this.unlocked = unlocked
                                })
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                this.stars = currentNbt.getInt("stars", 0)
                this.recombobulated = currentNbt.getBoolean("recombobulated", false)
                this.reforge = SReforge.valueOf(currentNbt.getString("reforge", "NONE").uppercase(Locale.getDefault()))
                this.hotPotatoBooks = currentNbt.getInt("hotPotatoBooks", 0)
                this.fumingPotatoBooks = currentNbt.getInt("fumingPotatoBooks", 0)
                this.isDungeon = currentNbt.getBoolean("dungeon", false)
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

        // Calculate stats
        val stats = SStatManager(baseStats)

        //Stars (Should only apply to base stats)
        if (stars > 0) {
            stats.forEach { stat ->
                if (stat.type != SStatType.GEAR_SCORE)
                    stat.value *= 1 + (0.02 * stars)
            }
        }

        // Apply reforge bonuses
        if (reforge != SReforge.NONE) {
            val bonuses = reforge.getStatByRarity(effectiveRarity)
            bonuses.forEach { bonusStat -> stats.add(bonusStat) }
        }

        // Apply gemstone bonuses
        val gemstoneStats: MutableList<SStat> = mutableListOf()
        gemstoneSlots.forEach { slot ->
            if (slot.gemstone != null) {
                val gemstoneStat = slot.gemstone!!.getStatByRarity(effectiveRarity)
                stats.add(gemstoneStat)
                val existingGemstoneStat = gemstoneStats.find { it.type == gemstoneStat.type }
                if (existingGemstoneStat != null) {
                    existingGemstoneStat.value += gemstoneStat.value
                } else {
                    gemstoneStats.add(SStat(gemstoneStat.type, gemstoneStat.value))
                }
            }
        }

        // Apply hot potato books
        if (hotPotatoBooks + fumingPotatoBooks > 0) {
            if (type.categories.contains(SItemCategory.WEAPONS)) {
                stats.add(SStatType.DAMAGE, (hotPotatoBooks + fumingPotatoBooks) * 2.0)
                stats.add(SStatType.STRENGTH, (hotPotatoBooks + fumingPotatoBooks) * 2.0)
            }
            if (type.categories.contains(SItemCategory.ARMOR)) {
                stats.add(SStatType.DEFENSE, (hotPotatoBooks + fumingPotatoBooks) * 2.0)
                stats.add(SStatType.HEALTH, (hotPotatoBooks + fumingPotatoBooks) * 4.0)
            }
        }

        fun statText(stat: SStat): MutableText {
            return Text.literal("${if (stat.type.display == SStatTypeDisplay.BASE || stat.type.display == SStatTypeDisplay.PERCENTAGE) if (stat.value >= 0) "+" else "-" else ""}${stat.formattedValue}${if (stat.type.display == SStatTypeDisplay.PERCENTAGE) "%" else ""}")
        }

        // Lore Builder
        val loreBuilder = LoreBuilder()
        if (hasRecipe)
            loreBuilder.addLine(
                Text.literal("Right-click to view recipes!").formatted(Formatting.YELLOW)
            )

        stats.sort()
        stats.forEach { stat ->
            var reforgeText = Text.empty()
            if (reforge != SReforge.NONE) {
                val reforgeStat = reforge.getStatByRarity(effectiveRarity).find { it.type == stat.type }
                reforgeText = if (reforgeStat != null) {
                    Text.literal(" (${statText(reforgeStat).string})")
                        .formatted(Formatting.BLUE)
                } else {
                    Text.empty()
                }
            }

            var gemstoneText = Text.empty()
            if (gemstoneSlots.hasAny()) {
                gemstoneSlots.forEach { slot ->
                    if (slot.gemstone != null && slot.gemstone!!.getStatByRarity(effectiveRarity).type == stat.type) {
                        slot.gemstone!!.getStatByRarity(effectiveRarity)
                        gemstoneText =
                            Text.literal(" (${statText(gemstoneStats.find { it.type == stat.type }!!).string})")
                                .formatted(Formatting.LIGHT_PURPLE)
                    }
                }
            }

            var potatoText = Text.empty()
            if (hotPotatoBooks + fumingPotatoBooks > 0) {
                if (type.categories.contains(SItemCategory.WEAPONS)) {
                    if (stat.type == SStatType.DAMAGE || stat.type == SStatType.STRENGTH) {
                        potatoText = Text.literal(" (+${(hotPotatoBooks + fumingPotatoBooks) * 2})")
                            .formatted(Formatting.YELLOW)
                    }
                }
                if (type.categories.contains(SItemCategory.ARMOR)) {
                    if (stat.type == SStatType.DEFENSE) {
                        potatoText = Text.literal(" (+${(hotPotatoBooks + fumingPotatoBooks) * 2})")
                            .formatted(Formatting.YELLOW)
                    } else if (stat.type == SStatType.HEALTH) {
                        potatoText = Text.literal(" (+${(hotPotatoBooks + fumingPotatoBooks) * 4})")
                            .formatted(Formatting.YELLOW)
                    }
                }
            }


            // Stat Line
            loreBuilder.addLine(
                Text.literal(stat.type.displayName + ": ")
                    .formatted(Formatting.GRAY)
                    .append(statText(stat).formatted(stat.type.formatting)).append(potatoText).append(reforgeText)
                    .append(gemstoneText)
            )

        }

        // gemstone slots
        if (gemstoneSlots.hasAny()) {
            val text = Text.empty()
            gemstoneSlots.forEach { slot ->
                text.append(slot.getText())
            }
            loreBuilder.addLine(text)
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
                currentNbt.putString("gemstoneSlots", gemstoneSlots.toString())
                currentNbt.putInt("hotPotatoBooks", hotPotatoBooks)
                currentNbt.putInt("fumingPotatoBooks", fumingPotatoBooks)
                currentNbt.putBoolean("dungeon", isDungeon)
            })
        }

        // Hide tooltip display
        val emptySequencedSet: SequencedSet<ComponentType<*>> = LinkedHashSet()
        emptySequencedSet.add(DataComponentTypes.ATTRIBUTE_MODIFIERS)
        val tooltipDisplayComponent = TooltipDisplayComponent(false, emptySequencedSet)
        itemStack.set(DataComponentTypes.TOOLTIP_DISPLAY, tooltipDisplayComponent)
    }


}
