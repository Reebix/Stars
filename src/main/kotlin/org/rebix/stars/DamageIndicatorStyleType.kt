package org.rebix.stars

import net.minecraft.util.Formatting

enum class DamageIndicatorStyleType(val formatting: Set<Formatting>, val prefix: String = "", val suffix: String = "") {
    NORMAL(setOf(Formatting.WHITE)),
    CRIT(setOf(Formatting.WHITE, Formatting.WHITE, Formatting.YELLOW, Formatting.GOLD, Formatting.RED), "✧", "✧"),
}