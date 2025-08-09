package org.rebix.stars.hooks

import net.minecraft.block.BlockState
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

class FireBlockMixinHook {
    fun scheduledTick(world: ServerWorld, pos: BlockPos, state: BlockState, random: Random, ci: CallbackInfo) {
    }
}