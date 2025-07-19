package org.rebix.stars.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import org.rebix.stars.hooks.ScreenHandlerMixinHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public class ScreenHandlerMixin {
    @Unique
    ScreenHandlerMixinHook screenHandlerMixinHook = new ScreenHandlerMixinHook();

    @Inject(method = "onSlotClick", at = @At("HEAD"), cancellable = true)
    private void onClickSlot(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        screenHandlerMixinHook.onClickSlot(slotIndex, button, actionType, player, ci);

    }
}
