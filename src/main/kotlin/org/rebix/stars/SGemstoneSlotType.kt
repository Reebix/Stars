package org.rebix.stars

import net.minecraft.util.Formatting

enum class SGemstoneSlotType(val color: Formatting, val icon: String, val usable: Set<SGemstoneType>) {
    JADE(Formatting.GREEN, "☘", setOf(SGemstoneType.FINE_JADE)),
    AMBER(Formatting.GOLD, "⸕", setOf(SGemstoneType.FINE_AMBER)),
    TOPAZ(Formatting.YELLOW, "✧", setOf(SGemstoneType.FINE_TOPAZ)),
    SAPPHIRE(Formatting.BLUE, "✎", setOf(SGemstoneType.FINE_SAPPHIRE)),
    AMETHYST(Formatting.DARK_PURPLE, "❈", setOf(SGemstoneType.FINE_AMETHYST)),
    JASPER(Formatting.LIGHT_PURPLE, "❁", setOf(SGemstoneType.FINE_JASPER)),
    OPAL(Formatting.WHITE, "❂", setOf(SGemstoneType.FINE_OPAL)),
    RUBY(Formatting.RED, "❤", setOf(SGemstoneType.FINE_RUBY)),
    CITRINE(Formatting.DARK_RED, "☘", setOf(SGemstoneType.FINE_CITRINE)),
    AQUAMARINE(Formatting.DARK_AQUA, "α", setOf(SGemstoneType.FINE_AQUAMARINE)),
    PERIDOT(Formatting.DARK_GREEN, "☘", setOf(SGemstoneType.FINE_PERIDOT)),
    ONYX(Formatting.GRAY, "☣", setOf(SGemstoneType.FINE_ONYX)),
    CHISEL(
        Formatting.GOLD, "❥", setOf(
            SGemstoneType.FINE_CITRINE,
            SGemstoneType.FINE_AQUAMARINE,
            SGemstoneType.FINE_ONYX,
            SGemstoneType.FINE_PERIDOT
        )
    ),
    COMBAT(
        Formatting.DARK_RED, "⚔",
        setOf(
            SGemstoneType.FINE_RUBY,
            SGemstoneType.FINE_SAPPHIRE,
            SGemstoneType.FINE_AMETHYST,
            SGemstoneType.FINE_JASPER,
            SGemstoneType.FINE_OPAL,
            SGemstoneType.FINE_ONYX
        )
    ),
    DEFENSIVE(
        Formatting.GREEN, "☤",
        setOf(
            SGemstoneType.FINE_AMETHYST,
            SGemstoneType.FINE_RUBY,
            SGemstoneType.FINE_OPAL
        )
    ),
    MINING(
        Formatting.DARK_PURPLE, "✦",
        setOf(
            SGemstoneType.FINE_JADE,
            SGemstoneType.FINE_AMBER,
            SGemstoneType.FINE_TOPAZ
        )
    ),
    UNIVERSAL(
        Formatting.WHITE, "❂",
        setOf(
            SGemstoneType.FINE_JADE,
            SGemstoneType.FINE_AMBER,
            SGemstoneType.FINE_TOPAZ,
            SGemstoneType.FINE_SAPPHIRE,
            SGemstoneType.FINE_AMETHYST,
            SGemstoneType.FINE_JASPER,
            SGemstoneType.FINE_OPAL,
            SGemstoneType.FINE_RUBY,
            SGemstoneType.FINE_CITRINE,
            SGemstoneType.FINE_AQUAMARINE,
            SGemstoneType.FINE_PERIDOT,
            SGemstoneType.FINE_ONYX
        )
    ),
    OFFENSIVE(
        Formatting.BLUE, "☠",
        setOf(
            SGemstoneType.FINE_SAPPHIRE,
            SGemstoneType.FINE_JASPER
        )
    ),
}