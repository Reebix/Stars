package org.rebix.stars.mixin;

import net.minecraft.world.World;
import org.rebix.stars.hooks.WorldMixinHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(World.class)
public class WorldMixin {

    @Unique
    WorldMixinHook worldMixinHook = new WorldMixinHook();

}
