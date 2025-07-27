package org.rebix.stars

import net.minecraft.entity.decoration.InteractionEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.ArrowEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.Hand
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

    fun update() {
        val living = this.baseEntity
        if (living is InteractionEntity) {
            // Bounding Box um 0.5 Blöcke in Y-Richtung erweitern
            val searchBox = living.boundingBox.expand(0.0, 0.5, 0.0)
            // Alle Pfeile im Suchbereich holen
            val arrows = world.getEntitiesByClass(ArrowEntity::class.java, searchBox) { true }
            for (arrow in arrows) {
                // Wenn Pfeil aktiv und sich überschneidet
                if (arrow.isAlive && arrow.boundingBox.intersects(living.boundingBox)) {
                    // Schaden basierend auf Pfeil-Damage anwenden

                    if (arrow.owner?.isPlayer == true) {
                        val player = arrow.owner!! as PlayerEntity
                        val sItem = SItem(player.getStackInHand(Hand.MAIN_HAND))

                        val handler = SStatHandler()
                        handler.statManager = sItem.effectiveStats
                        val damage = handler.calcDamage()
                        damage(damage.first, player.pos, damage.second)
                        // Pfeil entfernen
                        arrow.kill(arrow.world as ServerWorld)
                    }
                }
            }
        } else {
            position = baseEntity!!.pos
        }
    }


}