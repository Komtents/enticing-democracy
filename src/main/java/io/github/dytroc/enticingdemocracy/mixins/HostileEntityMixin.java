package io.github.dytroc.enticingdemocracy.mixins;

import io.github.dytroc.enticingdemocracy.EnticingDemocracy;
import io.github.dytroc.enticingdemocracy.event.Events;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HostileEntity.class)
public class HostileEntityMixin {
    @Inject(at = @At("RETURN"), method = "isAngryAt", cancellable = true)
    private void isAngry(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (EnticingDemocracy.hasTemporaryEvent(Events.notTargeted)) cir.setReturnValue(false);
    }
}
