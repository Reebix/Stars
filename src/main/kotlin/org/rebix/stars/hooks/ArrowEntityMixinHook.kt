package org.rebix.stars.hooks

import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.ArrowEntity
import net.minecraft.util.Hand
import org.rebix.stars.Stars.Companion.entityMap
import org.rebix.stars.combat.SCombatEntity
import org.rebix.stars.item.SItem
import org.rebix.stars.stats.SStatHandler
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

class ArrowEntityMixinHook {
    fun onHit(arrowEntity: ArrowEntity, target: LivingEntity, ci: CallbackInfo) {
        val sEntity = entityMap[target.uuid]

        val combatEntity = sEntity as? SCombatEntity
        if (arrowEntity.owner?.isPlayer == true && combatEntity != null) {
            val player = arrowEntity.owner!! as PlayerEntity
            val sItem = SItem(player.getStackInHand(Hand.MAIN_HAND))

            val handler = SStatHandler()
            handler.statManager = sItem.effectiveStats
            val damage = handler.calcDamage()
            combatEntity.damage(damage.first, player.pos, damage.second)
        }
    }
}