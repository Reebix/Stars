package org.rebix.stars.stats

import net.minecraft.util.Formatting

enum class SStatType(
    val displayName: String,
    val formatting: Formatting,
    val display: SStatTypeDisplay = SStatTypeDisplay.BASE
) {
    GEAR_SCORE("Gear Score", Formatting.LIGHT_PURPLE, SStatTypeDisplay.NONE),
    DAMAGE("Damage", Formatting.RED),
    STRENGTH("Strength", Formatting.RED),
    CRIT_CHANCE("Crit Chance", Formatting.RED, SStatTypeDisplay.PERCENTAGE),
    CRIT_DAMAGE("Crit Damage", Formatting.RED, SStatTypeDisplay.PERCENTAGE),
    BONUS_ATTACK_SPEED("Bonus Attack Speed", Formatting.RED, SStatTypeDisplay.PERCENTAGE),
    ABILITY_DAMAGE("Ability Damage", Formatting.RED, SStatTypeDisplay.PERCENTAGE),
    HEALTH("Health", Formatting.GREEN),
    DEFENSE("Defense", Formatting.GREEN),
    SPEED("Speed", Formatting.GREEN),
    INTELLIGENCE("Intelligence", Formatting.GREEN),
    TRUE_DEFENSE("True Defense", Formatting.GREEN),
    FEROCITY("Ferocity", Formatting.GREEN),
    HEALTH_REGEN("Health Regen", Formatting.GREEN),
    VITALITY("Vitality", Formatting.GREEN),
    MINING_SPEED("Mining Speed", Formatting.GOLD),
    PRISTINE("Pristine", Formatting.DARK_PURPLE),
    MINING_FORTUNE("Mining Fortune", Formatting.GOLD),
    FISHING_SPEED("Fishing Speed", Formatting.AQUA),
    FORAGING_FORTUNE("Foraging Fortune", Formatting.GOLD),
    FARMING_FORTUNE("Farming Fortune", Formatting.GOLD),
    SHOT_COOLDOWN("Shot Cooldown", Formatting.GREEN, SStatTypeDisplay.SECONDS),
    SWEEP("Sweep", Formatting.GREEN),

}