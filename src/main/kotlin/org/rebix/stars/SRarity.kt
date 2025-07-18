package org.rebix.stars

import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting

enum class SRarity(val formatting: Formatting) {
    COMMON(Formatting.WHITE),
    UNCOMMON(Formatting.GREEN),
    RARE(Formatting.BLUE),
    EPIC(Formatting.DARK_PURPLE),
    LEGENDARY(Formatting.GOLD),
    MYTHIC(Formatting.LIGHT_PURPLE),
    DIVINE(Formatting.AQUA),
    SPECIAL(Formatting.RED),
    VERY_SPECIAL(Formatting.RED),
    ULTIMATE(Formatting.DARK_RED),
    ADMIN(Formatting.DARK_RED);

    fun isCommon(): Boolean = this == COMMON
    fun isUncommon(): Boolean = this == UNCOMMON
    fun isRare(): Boolean = this == RARE
    fun isEpic(): Boolean = this == EPIC
    fun isLegendary(): Boolean = this == LEGENDARY
    fun isMythic(): Boolean = this == MYTHIC
    fun isDivine(): Boolean = this == DIVINE
    fun isSpecial(): Boolean = this == SPECIAL
    fun isVerySpecial(): Boolean = this == VERY_SPECIAL
    fun isUltimate(): Boolean = this == ULTIMATE
    fun isAdmin(): Boolean = this == ADMIN

    fun getText(): MutableText {
        return Text.literal(name).formatted(Formatting.BOLD).formatted(formatting)
    }
}