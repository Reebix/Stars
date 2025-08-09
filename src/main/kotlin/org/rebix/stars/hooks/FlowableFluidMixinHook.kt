package org.rebix.stars.hooks

import net.minecraft.block.BlockState
import net.minecraft.fluid.FluidState
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import org.rebix.stars.dimensions.DimensionTags
import org.rebix.stars.dimensions.ModDimensions
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

class FlowableFluidMixinHook {
    fun onScheduledTick(
        world: ServerWorld,
        pos: BlockPos,
        blockState: BlockState,
        fluidState: FluidState,
        ci: CallbackInfo
    ) {
        if (ModDimensions.DIMENSION_DICT[world.registryKey]?.contains(DimensionTags.NO_FLUIDS) == true) {
            ci.cancel()
        }

    }
}