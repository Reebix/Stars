package org.rebix.stars.combat

import net.minecraft.util.Formatting

enum class DamageIndicatorStyleType(
    val formatting: List<Formatting>,
    val prefix: String = "",
    val suffix: String = ""
) {
    NORMAL(listOf(Formatting.GRAY)),
    CRIT(
        listOf(Formatting.WHITE, Formatting.WHITE, Formatting.YELLOW, Formatting.GOLD, Formatting.RED, Formatting.RED),
        "✧",
        "✧"
    ),
}