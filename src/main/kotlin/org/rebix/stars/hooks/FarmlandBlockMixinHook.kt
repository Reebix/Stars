package org.rebix.stars.hooks

import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.rebix.stars.ModDimensions
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

class FarmlandBlockMixinHook {
    fun onLandedUpon(
        world: World,
        state: BlockState,
        pos: BlockPos,
        entity: Entity,
        fallDistance: Double,
        ci: CallbackInfo
    ) {

        if (world.registryKey == ModDimensions.HUB_DIMENSION_KEY || world.registryKey == ModDimensions.NETHER_DIMENSION_KEY) {
            ci.cancel()
        }


    }

    fun setToDirt(entity: Entity, state: BlockState, world: World, pos: BlockPos, ci: CallbackInfo) {
        if (world.registryKey == ModDimensions.HUB_DIMENSION_KEY || world.registryKey == ModDimensions.NETHER_DIMENSION_KEY) {
            ci.cancel()
        }
    }
}