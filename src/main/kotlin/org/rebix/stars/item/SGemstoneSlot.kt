package org.rebix.stars.item

import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting

class SGemstoneSlot(val type: SGemstoneSlotType) {
    var gemstone: SGemstoneType? = null
    var unlocked: Boolean = false

    fun getText(): MutableText {
        val text = Text.empty()
        if (gemstone == null) {
            text.append(
                Text.literal(" [").formatted(Formatting.DARK_GRAY)
                    .append(Text.literal(type.icon).formatted(if (unlocked) Formatting.GRAY else Formatting.DARK_GRAY))
                    .append("]")
            )
        } else {
            text.append(
                Text.literal(" [").formatted(gemstone!!.rarity.formatting)
                    .append(
                        Text.literal(type.icon).formatted(gemstone!!.color)
                    ).append("]")
            )
        }
        return text
    }

    override fun toString(): String {
        return "SGemstoneSlot(type=$type; gemstone=$gemstone; unlocked=$unlocked)"
    }
}