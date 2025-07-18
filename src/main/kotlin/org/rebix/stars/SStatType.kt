package org.rebix.stars

import net.minecraft.util.Formatting

enum class SStatType(
    val displayName: String,
    val formatting: Formatting,
    val display: SStatTypeDisplay = SStatTypeDisplay.BASE
) {
    GEAR_SCORE("Gear Score", Formatting.LIGHT_PURPLE, SStatTypeDisplay.NONE),
    DAMAGE("Damage", Formatting.RED),
    STRENGTH("Strength", Formatting.RED),
    INTELLIGENCE("Intelligence", Formatting.GREEN),
    FEROCITY("Ferocity", Formatting.GREEN),
    DEFENSE("Defense", Formatting.GREEN);

}