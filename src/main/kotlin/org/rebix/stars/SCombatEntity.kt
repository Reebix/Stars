package org.rebix.stars

import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class SCombatEntity(
    type: SEntityType,
    world: World,
    name: Text = Text.empty(),
    position: Vec3d = Vec3d.ZERO,
    _health: Long = 100,
    _maxHealth: Long = 100
) : SLivingEntity(type, world, name, position, _health, _maxHealth) {
    val statHandler = SStatHandler()

    fun damage(
        damage: Long,
        attacker: Vec3d? = null,
        damageIndicatorStyleType: DamageIndicatorStyleType = DamageIndicatorStyleType.NORMAL,
    ) {
        val finalDamage = statHandler.calcDamageTaken(damage)
        onHit(damage = finalDamage, attackerPosition = attacker, hitType = damageIndicatorStyleType)

    }


}