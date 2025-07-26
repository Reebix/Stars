package org.rebix.stars.hooks

import net.minecraft.entity.Entity
import net.minecraft.entity.mob.ZombieEntity
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

class ZombieEntityMixinHook {
    fun burnsInDaylight(ZombieEntity: ZombieEntity, cir: CallbackInfoReturnable<Boolean>) {
        cir.returnValue = false
    }

    fun tryAttack(ZombieEntity: ZombieEntity, target: Entity, cir: CallbackInfoReturnable<Boolean>) {
//        println("ZombieEntityMixinHook: tryAttack called for ${ZombieEntity.type}")
//        target.damage(
//            target.world as ServerWorld,
//            ZombieEntity.damageSources.generic(),
//            1f
//        )
    }
}