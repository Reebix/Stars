package org.rebix.stars.hooks

import net.minecraft.block.BlockState
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random
import net.minecraft.world.World
import org.rebix.stars.ModDimensions
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

class CropBlockMixinHook {
    fun applyGrowth(world: World, pos: BlockPos, state: BlockState, ci: CallbackInfo) {

        // Prevent crop growth in the hub dimension
        if (world.registryKey == ModDimensions.HUB_DIMENSION_KEY || world.registryKey == ModDimensions.NETHER_DIMENSION_KEY) {
            ci.cancel()
        }
    }

    fun randomTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random, ci: CallbackInfo) {
        // Prevent random ticks for crops in the hub dimension
        if (world.registryKey == ModDimensions.HUB_DIMENSION_KEY || world.registryKey == ModDimensions.NETHER_DIMENSION_KEY) {
            ci.cancel()
        }
    }
}