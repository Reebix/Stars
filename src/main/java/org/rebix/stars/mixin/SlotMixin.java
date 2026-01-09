package org.rebix.stars.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public class SlotMixin {

    @Inject(method = "onTake", at = @At("HEAD"), cancellable = true)
    private void onTake(int amount, CallbackInfo ci) {
//        ci.cancel();
    }

    @Inject(method = "onTakeItem", at = @At("HEAD"), cancellable = true)
    private void onTakeItem(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
//        ci.cancel();
    }

    @Inject(method = "takeStack", at = @At("HEAD"), cancellable = true)
    private void takeStack(int amount, CallbackInfoReturnable<ItemStack> cir) {
//        System.out.println("takeStack called with amount: " + amount);
//        cir.setReturnValue(ItemStack.EMPTY);
    }
}
