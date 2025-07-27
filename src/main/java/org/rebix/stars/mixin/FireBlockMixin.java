package org.rebix.stars.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.FireBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.rebix.stars.hooks.FireBlockMixinHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireBlock.class)
public class FireBlockMixin {
    @Unique
    FireBlockMixinHook fireBlockMixinHook = new FireBlockMixinHook();

    @Inject(method = "scheduledTick", at = @At("HEAD"), cancellable = true)
    private void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        fireBlockMixinHook.scheduledTick(world, pos, state, random, ci);
    }
}
