package org.rebix.stars.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.rebix.stars.hooks.FlowableFluidMixinHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FlowableFluid.class)
public class FlowableFluidMixin {

    @Unique
    FlowableFluidMixinHook flowableFluidMixinHook = new FlowableFluidMixinHook();

    @Inject(
            method = "onScheduledTick",
            at = @At("HEAD"),
            cancellable = true)
    private void onScheduledTick(ServerWorld world, BlockPos pos, BlockState blockState, FluidState fluidState, CallbackInfo ci) {
        flowableFluidMixinHook.onScheduledTick(world, pos, blockState, fluidState, ci);
    }
}

