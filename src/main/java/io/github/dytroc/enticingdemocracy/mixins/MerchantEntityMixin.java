package io.github.dytroc.enticingdemocracy.mixins;

import io.github.dytroc.enticingdemocracy.EnticingDemocracy;
import io.github.dytroc.enticingdemocracy.event.Events;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.village.TradeOfferList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MerchantEntity.class)
public class MerchantEntityMixin {
    @Inject(at = @At("HEAD"), method = "setCustomer", cancellable = true)
    private void onSetCustomer(PlayerEntity customer, CallbackInfo ci) {
        if (EnticingDemocracy.hasPermanentEvent(Events.noTrade)) ci.cancel();
    }

    @Inject(at = @At("RETURN"), method = "getOffers", cancellable = true)
    private void onGetOffers(CallbackInfoReturnable<TradeOfferList> cir) {
        if (EnticingDemocracy.hasPermanentEvent(Events.noTrade)) cir.setReturnValue(new TradeOfferList());
    }
}
