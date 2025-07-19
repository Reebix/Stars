package org.rebix.stars

import net.minecraft.util.Formatting

enum class SGemstoneType(
    val rarity: SRarity,
    val icon: String,
    val color: Formatting,
    val bonuses: HashMap<SRarity, SStat>
) {
    FINE_RUBY(
        SRarity.RARE, "❤",
        Formatting.RED, hashMapOf(
            SRarity.COMMON to SStat(SStatType.HEALTH, 4),
            SRarity.UNCOMMON to SStat(SStatType.HEALTH, 5),
            SRarity.RARE to SStat(SStatType.HEALTH, 6),
            SRarity.EPIC to SStat(SStatType.HEALTH, 8),
            SRarity.LEGENDARY to SStat(SStatType.HEALTH, 10),
            SRarity.MYTHIC to SStat(SStatType.HEALTH, 14)
        )
    ),
    FINE_AMBER(
        SRarity.RARE, "⸕",
        Formatting.GOLD, hashMapOf(
            SRarity.COMMON to SStat(SStatType.MINING_SPEED, 10),
            SRarity.UNCOMMON to SStat(SStatType.MINING_SPEED, 14),
            SRarity.RARE to SStat(SStatType.MINING_SPEED, 20),
            SRarity.EPIC to SStat(SStatType.MINING_SPEED, 28),
            SRarity.LEGENDARY to SStat(SStatType.MINING_SPEED, 36),
            SRarity.MYTHIC to SStat(SStatType.MINING_SPEED, 45),
            SRarity.DIVINE to SStat(SStatType.MINING_SPEED, 54),
        )
    ),
    FINE_TOPAZ(
        SRarity.RARE, "✧",
        Formatting.YELLOW, hashMapOf(
            SRarity.COMMON to SStat(SStatType.PRISTINE, 1.2),
            SRarity.UNCOMMON to SStat(SStatType.PRISTINE, 1.2),
            SRarity.RARE to SStat(SStatType.PRISTINE, 1.2),
            SRarity.EPIC to SStat(SStatType.PRISTINE, 1.2),
            SRarity.LEGENDARY to SStat(SStatType.PRISTINE, 1.2),
            SRarity.MYTHIC to SStat(SStatType.PRISTINE, 1.2),
            SRarity.DIVINE to SStat(SStatType.PRISTINE, 1.3),
        )
    ),
    FINE_JADE(
        SRarity.RARE, "☘",
        Formatting.GREEN, hashMapOf(
            SRarity.COMMON to SStat(SStatType.MINING_FORTUNE, 5),
            SRarity.UNCOMMON to SStat(SStatType.MINING_FORTUNE, 7),
            SRarity.RARE to SStat(SStatType.MINING_FORTUNE, 10),
            SRarity.EPIC to SStat(SStatType.MINING_FORTUNE, 15),
            SRarity.LEGENDARY to SStat(SStatType.MINING_FORTUNE, 20),
            SRarity.MYTHIC to SStat(SStatType.MINING_FORTUNE, 25),
            SRarity.DIVINE to SStat(SStatType.MINING_FORTUNE, 30),
        )
    ),
    FINE_SAPPHIRE(
        SRarity.RARE, "✎",
        Formatting.AQUA, hashMapOf(
            SRarity.COMMON to SStat(SStatType.INTELLIGENCE, 7),
            SRarity.UNCOMMON to SStat(SStatType.INTELLIGENCE, 8),
            SRarity.RARE to SStat(SStatType.INTELLIGENCE, 9),
            SRarity.EPIC to SStat(SStatType.INTELLIGENCE, 10),
            SRarity.LEGENDARY to SStat(SStatType.INTELLIGENCE, 11),
            SRarity.MYTHIC to SStat(SStatType.INTELLIGENCE, 12),
        )
    ),
    PERFECT_SAPPHIRE(
        SRarity.LEGENDARY, "✎",
        Formatting.AQUA, hashMapOf(
            SRarity.COMMON to SStat(SStatType.INTELLIGENCE, 12),
            SRarity.UNCOMMON to SStat(SStatType.INTELLIGENCE, 14),
            SRarity.RARE to SStat(SStatType.INTELLIGENCE, 17),
            SRarity.EPIC to SStat(SStatType.INTELLIGENCE, 20),
            SRarity.LEGENDARY to SStat(SStatType.INTELLIGENCE, 24),
            SRarity.MYTHIC to SStat(SStatType.INTELLIGENCE, 30),
        )
    ),
    FINE_AMETHYST(
        SRarity.RARE, "❈",
        Formatting.GREEN, hashMapOf(
            SRarity.COMMON to SStat(SStatType.DEFENSE, 4),
            SRarity.UNCOMMON to SStat(SStatType.DEFENSE, 5),
            SRarity.RARE to SStat(SStatType.DEFENSE, 6),
            SRarity.EPIC to SStat(SStatType.DEFENSE, 8),
            SRarity.LEGENDARY to SStat(SStatType.DEFENSE, 10),
            SRarity.MYTHIC to SStat(SStatType.DEFENSE, 14),
        )
    ),
    FINE_JASPER(
        SRarity.RARE, "❁",
        Formatting.LIGHT_PURPLE, hashMapOf(
            SRarity.COMMON to SStat(SStatType.STRENGTH, 3),
            SRarity.UNCOMMON to SStat(SStatType.STRENGTH, 3),
            SRarity.RARE to SStat(SStatType.STRENGTH, 4),
            SRarity.EPIC to SStat(SStatType.STRENGTH, 5),
            SRarity.LEGENDARY to SStat(SStatType.STRENGTH, 6),
            SRarity.MYTHIC to SStat(SStatType.STRENGTH, 7),
        )
    ),
    FINE_OPAL(
        SRarity.RARE, "❂",
        Formatting.WHITE, hashMapOf(
            SRarity.COMMON to SStat(SStatType.TRUE_DEFENSE, 3),
            SRarity.UNCOMMON to SStat(SStatType.TRUE_DEFENSE, 3),
            SRarity.RARE to SStat(SStatType.TRUE_DEFENSE, 3),
            SRarity.EPIC to SStat(SStatType.TRUE_DEFENSE, 4),
            SRarity.LEGENDARY to SStat(SStatType.TRUE_DEFENSE, 4),
            SRarity.MYTHIC to SStat(SStatType.TRUE_DEFENSE, 5),
        )
    ),
    FINE_AQUAMARINE(
        SRarity.RARE, "☂",
        Formatting.DARK_AQUA, hashMapOf(
            SRarity.COMMON to SStat(SStatType.FISHING_SPEED, 1.5),
            SRarity.UNCOMMON to SStat(SStatType.FISHING_SPEED, 1.5),
            SRarity.RARE to SStat(SStatType.FISHING_SPEED, 2.0),
            SRarity.EPIC to SStat(SStatType.FISHING_SPEED, 2.0),
            SRarity.LEGENDARY to SStat(SStatType.FISHING_SPEED, 2.5),
            SRarity.MYTHIC to SStat(SStatType.FISHING_SPEED, 3.0),
        )
    ),
    FINE_CITRINE(
        SRarity.RARE, "☘",
        Formatting.DARK_RED, hashMapOf(
            SRarity.COMMON to SStat(SStatType.FORAGING_FORTUNE, 1.5),
            SRarity.UNCOMMON to SStat(SStatType.FORAGING_FORTUNE, 2.0),
            SRarity.RARE to SStat(SStatType.FORAGING_FORTUNE, 3.0),
            SRarity.EPIC to SStat(SStatType.FORAGING_FORTUNE, 4.0),
            SRarity.LEGENDARY to SStat(SStatType.FORAGING_FORTUNE, 5.0),
            SRarity.MYTHIC to SStat(SStatType.FORAGING_FORTUNE, 6.0)
        )
    ),
    FINE_ONYX(
        SRarity.RARE, "☣",
        Formatting.GRAY, hashMapOf(
            SRarity.COMMON to SStat(SStatType.CRIT_DAMAGE, 3),
            SRarity.UNCOMMON to SStat(SStatType.CRIT_DAMAGE, 3),
            SRarity.RARE to SStat(SStatType.CRIT_DAMAGE, 4),
            SRarity.EPIC to SStat(SStatType.CRIT_DAMAGE, 5),
            SRarity.LEGENDARY to SStat(SStatType.CRIT_DAMAGE, 6),
            SRarity.MYTHIC to SStat(SStatType.CRIT_DAMAGE, 8)
        )
    ),
    FINE_PERIDOT(
        SRarity.RARE, "☘",
        Formatting.DARK_GREEN, hashMapOf(
            SRarity.COMMON to SStat(SStatType.FARMING_FORTUNE, 1.5),
            SRarity.UNCOMMON to SStat(SStatType.FARMING_FORTUNE, 2.0),
            SRarity.RARE to SStat(SStatType.FARMING_FORTUNE, 3.0),
            SRarity.EPIC to SStat(SStatType.FARMING_FORTUNE, 4.0),
            SRarity.LEGENDARY to SStat(SStatType.FARMING_FORTUNE, 5.0),
            SRarity.MYTHIC to SStat(SStatType.FARMING_FORTUNE, 6.0)
        )
    );
    // displayName wie gehabt


    val displayName: String
        get() {
            val display = name.replace("_", " ")
            display.split(" ").forEach { word -> display.replaceFirstChar { it.uppercase() } }
            return display
        }

    fun getStatByRarity(rarity: SRarity): SStat {
        var bonus = this.bonuses[rarity]
        if (bonus == null) {
            bonus = bonuses[bonuses.keys.max()]
        }
        return bonus!!
    }


}