package org.rebix.stars.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import org.rebix.stars.hooks.ArrowEntityMixinHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArrowEntity.class)
public class ArrowEntityMixin {

    @Unique
    ArrowEntityMixinHook arrowEntityMixinHook = new ArrowEntityMixinHook();

    @Inject(method = "onHit", at = @At("HEAD"), cancellable = true)
    public void onHit(LivingEntity target, CallbackInfo ci) {
        arrowEntityMixinHook.onHit((ArrowEntity) (Object) this, target, ci);
    }
}
