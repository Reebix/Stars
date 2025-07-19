package org.rebix.stars

import net.minecraft.util.Formatting

enum class DamageIndicatorStyleType(formatting: Set<Formatting>) {
    NORMAL(setOf(Formatting.WHITE)),
    CRIT(setOf(Formatting.WHITE, Formatting.WHITE, Formatting.YELLOW, Formatting.GOLD, Formatting.RED)),
}