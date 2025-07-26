package org.rebix.stars.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import org.rebix.stars.hooks.DamageSourceMixinHook;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DamageSource.class)
public class DamageSourceMixin {

    @Unique
    DamageSourceMixinHook DamageSourceMixinHook = new DamageSourceMixinHook();
    @Shadow
    @Final
    private RegistryEntry<DamageType> type;

    @Inject(method = "isIn", at = @At("HEAD"), cancellable = true)
    public void isIn(TagKey<DamageType> tag, CallbackInfoReturnable<Boolean> cir) {
        DamageSourceMixinHook.isIn((DamageSource) (Object) this, tag, cir, this.type);
    }
}
