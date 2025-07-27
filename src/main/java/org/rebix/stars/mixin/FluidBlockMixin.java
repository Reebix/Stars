package org.rebix.stars.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.rebix.stars.hooks.FluidBlockMixinHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FluidBlock.class)
public class FluidBlockMixin {

    @Unique
    FluidBlockMixinHook fluidBlockMixinHook = new FluidBlockMixinHook();

    @Inject(
            method = "randomTick",
            at = @At("HEAD"),
            cancellable = true)
    private void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        fluidBlockMixinHook.randomTick(state, world, pos, random, ci);
    }

    @Inject(
            method = "neighborUpdate",
            at = @At("HEAD"),
            cancellable = true)
    private void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, WireOrientation wireOrientation, boolean notify, CallbackInfo ci) {
        fluidBlockMixinHook.neighborUpdate(state, world, pos, sourceBlock, wireOrientation, notify, ci);

    }

    @Inject(
            method = "hasRandomTicks",
            at = @At("HEAD"),
            cancellable = true)
    private void hasRandomTicks(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        fluidBlockMixinHook.hasRandomTicks(state, cir);

    }
}
