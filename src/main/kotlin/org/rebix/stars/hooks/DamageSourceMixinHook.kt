package org.rebix.stars.hooks

import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.damage.DamageType
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.registry.tag.DamageTypeTags
import net.minecraft.registry.tag.TagKey
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

class DamageSourceMixinHook {
    fun isIn(
        DamageSource: DamageSource,
        tag: TagKey<DamageType>,
        cir: CallbackInfoReturnable<Boolean>,
        type: RegistryEntry<DamageType>
    ) {
        if (tag == DamageTypeTags.BYPASSES_COOLDOWN)
            cir.returnValue = true
        else
            cir.returnValue = type.isIn(tag)
    }
}