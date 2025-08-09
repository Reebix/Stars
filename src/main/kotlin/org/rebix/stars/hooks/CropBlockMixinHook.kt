package org.rebix.stars.hooks

import net.minecraft.block.BlockState
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random
import net.minecraft.world.World
import org.rebix.stars.dimensions.DimensionTags
import org.rebix.stars.dimensions.ModDimensions
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

class CropBlockMixinHook {
    fun applyGrowth(world: World, pos: BlockPos, state: BlockState, ci: CallbackInfo) {
        if (ModDimensions.DIMENSION_DICT[world.registryKey]?.contains(DimensionTags.NO_GROWTH) == true) {
            ci.cancel()
        }

    }

    fun randomTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random, ci: CallbackInfo) {
        if (ModDimensions.DIMENSION_DICT[world.registryKey]?.contains(DimensionTags.NO_GROWTH) == true) {
            ci.cancel()
        }
    }
}