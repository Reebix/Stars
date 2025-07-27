package org.rebix.stars.hooks

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random
import net.minecraft.world.World
import net.minecraft.world.block.WireOrientation
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

class FluidBlockMixinHook {
    fun randomTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random, ci: CallbackInfo) {
    }

    fun neighborUpdate(
        state: BlockState,
        world: World,
        pos: BlockPos,
        sourceBlock: Block,
        wireOrientation: WireOrientation?,
        notify: Boolean,
        ci: CallbackInfo
    ) {

    }

    fun hasRandomTicks(state: BlockState, cir: CallbackInfoReturnable<Boolean>) {
    }

}