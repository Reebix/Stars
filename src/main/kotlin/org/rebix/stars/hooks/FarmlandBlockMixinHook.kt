package org.rebix.stars.hooks

import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.rebix.stars.dimensions.DimensionTags
import org.rebix.stars.dimensions.ModDimensions
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

class FarmlandBlockMixinHook {
    fun onLandedUpon(
        world: World?,
        state: BlockState?,
        pos: BlockPos,
        entity: Entity?,
        fallDistance: Double?,
        ci: CallbackInfo
    ) {
        if (ModDimensions.DIMENSION_DICT[world?.registryKey]?.contains(DimensionTags.NO_GROWTH) == true) {
            // Prevent crop growth in the hub dimension
            ci.cancel()
        }


    }

    fun setToDirt(entity: Entity?, state: BlockState?, world: World?, pos: BlockPos?, ci: CallbackInfo) {
        if (ModDimensions.DIMENSION_DICT[world?.registryKey]?.contains(DimensionTags.NO_GROWTH) == true) {
            // Prevent crop growth in the hub dimension
            ci.cancel()
        }
    }
}