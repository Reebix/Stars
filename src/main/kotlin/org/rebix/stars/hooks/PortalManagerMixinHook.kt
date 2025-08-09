package org.rebix.stars.hooks

import org.rebix.stars.dimensions.DimensionTags
import org.rebix.stars.dimensions.ModDimensions.Companion.DIMENSION_DICT

class PortalManagerMixinHook {
    fun tick(
        world: net.minecraft.server.world.ServerWorld,
        entity: net.minecraft.entity.Entity,
        canUsePortals: Boolean,
        cir: org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<Boolean>
    ) {
        if (DIMENSION_DICT[world.registryKey]?.contains(DimensionTags.NO_PORTAL) == true) {
            cir.returnValue = false
        }
    }
}