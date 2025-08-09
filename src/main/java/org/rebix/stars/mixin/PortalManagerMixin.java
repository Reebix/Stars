package org.rebix.stars.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.dimension.PortalManager;
import org.rebix.stars.hooks.PortalManagerMixinHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PortalManager.class)
public class PortalManagerMixin {
    @Unique
    PortalManagerMixinHook portalManagerMixinHook = new PortalManagerMixinHook();

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(ServerWorld world, Entity entity, boolean canUsePortals, CallbackInfoReturnable<Boolean> cir) {
        portalManagerMixinHook.tick(world, entity, canUsePortals, cir);
    }
}
