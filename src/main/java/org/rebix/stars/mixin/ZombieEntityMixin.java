package org.rebix.stars.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.server.world.ServerWorld;
import org.rebix.stars.hooks.ZombieEntityMixinHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieEntity.class)
public class ZombieEntityMixin {
    @Unique
    ZombieEntityMixinHook zombieEntityMixinHook = new ZombieEntityMixinHook();

    @Inject(method = "burnsInDaylight", at = @At("HEAD"), cancellable = true)
    public void burnsInDaylight(CallbackInfoReturnable<Boolean> cir) {
        zombieEntityMixinHook.burnsInDaylight((ZombieEntity) (Object) this, cir);
    }

    @Inject(method = "tryAttack", at = @At("HEAD"), cancellable = true)
    public void tryAttack(ServerWorld world, Entity target, CallbackInfoReturnable<Boolean> cir) {
        zombieEntityMixinHook.tryAttack((ZombieEntity) (Object) this, target, cir);
    }
}
