package org.rebix.stars.hooks

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.screen.slot.SlotActionType
import org.rebix.stars.Stars
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

class ScreenHandlerMixinHook {
    fun onClickSlot(slotIndex: Int, button: Int, actionType: SlotActionType?, player: PlayerEntity, ci: CallbackInfo) {

        if (Stars.inventoryMap.containsKey(player.currentScreenHandler.syncId)) {
            val inventory = Stars.inventoryMap[player.currentScreenHandler.syncId] ?: return
            val stack = inventory.getStack(slotIndex)
            if (stack.isEmpty) {
                return
            }
            if (stack.name.string == " ") {
                ci.cancel()
            }
        }

    }
}