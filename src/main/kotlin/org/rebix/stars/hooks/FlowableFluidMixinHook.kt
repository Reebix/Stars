package org.rebix.stars.hooks

import net.minecraft.block.BlockState
import net.minecraft.fluid.FluidState
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import org.rebix.stars.ModDimensions
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

class FlowableFluidMixinHook {
    fun onScheduledTick(
        world: ServerWorld,
        pos: BlockPos,
        blockState: BlockState,
        fluidState: FluidState,
        ci: CallbackInfo
    ) {
        if (world.registryKey == ModDimensions.HUB_DIMENSION_KEY || world.registryKey == ModDimensions.NETHER_DIMENSION_KEY) {
            // Cancel the scheduled tick for fluids in the hub or test dimension
            ci.cancel()
        }
    }
}