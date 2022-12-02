package io.github.dytroc.enticingdemocracy.mixins;

import io.github.dytroc.enticingdemocracy.EnticingDemocracy;
import io.github.dytroc.enticingdemocracy.event.Events;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
    @Inject(at = @At("RETURN"), method = "cannotPickup", cancellable = true)
    private void cannotPickUp(CallbackInfoReturnable<Boolean> cir) {
        if (EnticingDemocracy.hasTemporaryEvent(Events.itemPickingIncapability)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(at = @At("HEAD"), method = "onPlayerCollision", cancellable = true)
    private void onPlayerCollision(PlayerEntity player, CallbackInfo ci) {
        if (EnticingDemocracy.hasTemporaryEvent(Events.itemPickingIncapability)) {
            ci.cancel();
        }
    }
}
