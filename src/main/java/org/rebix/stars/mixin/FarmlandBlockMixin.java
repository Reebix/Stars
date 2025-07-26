package org.rebix.stars.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.rebix.stars.hooks.FarmlandBlockMixinHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FarmlandBlock.class)
public class FarmlandBlockMixin {
    @Unique
    private static final FarmlandBlockMixinHook farmlandBlockMixinHook = new FarmlandBlockMixinHook();

    @Inject(method = "setToDirt"
            , at = @At("HEAD")
            , cancellable = true)
    private static void setToDirt(Entity entity, BlockState state, World world, BlockPos pos, CallbackInfo ci) {
        farmlandBlockMixinHook.setToDirt(entity, state, world, pos, ci);
    }

    @Inject(method = "onLandedUpon"
            , at = @At("HEAD")
            , cancellable = true)
    private void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, double fallDistance, CallbackInfo ci) {
        farmlandBlockMixinHook.onLandedUpon(world, state, pos, entity, fallDistance, ci);
    }
}
