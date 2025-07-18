package org.rebix.stars

import net.minecraft.component.type.LoreComponent
import net.minecraft.text.MutableText
import net.minecraft.text.Text

class LoreBuilder {
    private val lore: MutableList<Text> = mutableListOf()

    fun addLine(): LoreBuilder {
        lore.add(Text.empty())
        return this
    }

    fun addLine(line: MutableText, italic: Boolean = false): LoreBuilder {
        lore.add(line.styled { style ->
            style.withColor(line.style.color)
                .withItalic(italic)
                .withBold(line.style.isBold)
                .withStrikethrough(line.style.isStrikethrough)
                .withUnderline(line.style.isUnderlined)
        })
        return this
    }

    fun build(): LoreComponent {
        return LoreComponent(lore)
    }


}