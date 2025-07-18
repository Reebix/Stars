package org.rebix.stars

enum class SReforge(
    val displayName: String,
    val applicableTypes: Set<SItemType>,
    val bonuses: HashMap<SRarity, Set<SStat>>
) {
    NONE("none", setOf(SItemType.NONE), hashMapOf()),
    HEROIC(
        "Heroic", setOf(SItemType.SWORD),
        hashMapOf(
            SRarity.COMMON to setOf(
                SStat(SStatType.INTELLIGENCE, 40),
                SStat(SStatType.STRENGTH, 15),
                SStat(SStatType.BONUS_ATTACK_SPEED, 1)
            ),
            SRarity.UNCOMMON to setOf(
                SStat(SStatType.INTELLIGENCE, 50),
                SStat(SStatType.STRENGTH, 20),
                SStat(SStatType.BONUS_ATTACK_SPEED, 2)
            ),
            SRarity.RARE to setOf(
                SStat(SStatType.INTELLIGENCE, 65),
                SStat(SStatType.STRENGTH, 25),
                SStat(SStatType.BONUS_ATTACK_SPEED, 2)
            ),
            SRarity.EPIC to setOf(
                SStat(SStatType.INTELLIGENCE, 80),
                SStat(SStatType.STRENGTH, 32),
                SStat(SStatType.BONUS_ATTACK_SPEED, 3)
            ),
            SRarity.LEGENDARY to setOf(
                SStat(SStatType.INTELLIGENCE, 100),
                SStat(SStatType.STRENGTH, 40),
                SStat(SStatType.BONUS_ATTACK_SPEED, 5)
            ),
            SRarity.MYTHIC to setOf(
                SStat(SStatType.INTELLIGENCE, 125),
                SStat(SStatType.STRENGTH, 50),
                SStat(SStatType.BONUS_ATTACK_SPEED, 7)
            ),
        )
    ),
}