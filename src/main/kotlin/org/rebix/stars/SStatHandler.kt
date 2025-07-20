package org.rebix.stars

class SStatHandler {
    // https://wiki.hypixel.net/Damage
    var statManager = SStatManager()

    fun calcDamage(): Pair<Long, DamageIndicatorStyleType> {
        val baseDamage = statManager.getStat(SStatType.DAMAGE).value
        val strength = statManager.getStat(SStatType.STRENGTH).value
        val critChance = statManager.getStat(SStatType.CRIT_CHANCE).value
        val crit = Math.random() * 100 < critChance
        val style = if (crit) {
            DamageIndicatorStyleType.CRIT
        } else {
            DamageIndicatorStyleType.NORMAL
        }
        val critDamage = if (crit) statManager.getStat(SStatType.CRIT_DAMAGE).value else 0.0

        val additiveMultiplier = 1
        //{\displaystyle {\color {Gray}AdditiveMultiplier=1+(0.2+0.3)=1.5}}
        val multiplicativeMultiplier = 1
        //{\displaystyle {\color {Gray}MultiplicativeMultiplier=1*(1.1*2)=2.2}}
        val bonusModifier = 0

        return Pair(
            (((5 + baseDamage) * (1 + strength / 100.0) * additiveMultiplier * multiplicativeMultiplier + bonusModifier) * (1 + critDamage / 100)).toLong(),
            style
        )
    }

    fun calcAbilityDamage(baseAbilityDamage: Double, abilityScaling: Double): Pair<Long, DamageIndicatorStyleType> {
        val intelligence = statManager.getStat(SStatType.INTELLIGENCE).value
        val abilityDamage = statManager.getStat(SStatType.ABILITY_DAMAGE).value
        val additiveMultiplier = 1
        val multiplicativeMultiplier = 1
        val bonusModifier = 0


        return Pair(
            (((baseAbilityDamage * (1 + (intelligence / 100 * abilityScaling)) * additiveMultiplier * multiplicativeMultiplier + bonusModifier) * 1 + abilityDamage / 100).toLong()),
            DamageIndicatorStyleType.NORMAL
        )
    }

    fun calcDamageTaken(damage: Long): Long {
        val defense = statManager.getStat(SStatType.DEFENSE).value
        return damage * (1 - defense / (defense + 100.0)).toLong()
    }

    fun calcTrueDamageTaken(damage: Long): Long {
        val trueDefense = statManager.getStat(SStatType.TRUE_DEFENSE).value
        return damage * (1 - trueDefense / (trueDefense + 100.0)).toLong()
    }

}