package org.rebix.stars.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.rebix.stars.hooks.CropBlockMixinHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CropBlock.class)
public class CropBlockMixin {

    @Unique
    CropBlockMixinHook cropBlockMixinHook = new CropBlockMixinHook();

    @Inject(
            method = "applyGrowth",
            at = @At("HEAD"),
            cancellable = true)
    private void applyGrowth(World world, BlockPos pos, BlockState state, CallbackInfo ci) {
        cropBlockMixinHook.applyGrowth(world, pos, state, ci);
    }

    @Inject(
            method = "randomTick",
            at = @At("HEAD"),
            cancellable = true)
    private void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        cropBlockMixinHook.randomTick(state, world, pos, random, ci);
    }
}
