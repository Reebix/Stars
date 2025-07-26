package org.rebix.stars.hooks

import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.server.world.ServerWorld
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

class LivingEntityMixinHook {
    fun damage(
        LivingEntity: LivingEntity,
        world: ServerWorld,
        source: DamageSource,
        amount: Float,
        cir: CallbackInfoReturnable<Boolean>
    ) {

    }
}