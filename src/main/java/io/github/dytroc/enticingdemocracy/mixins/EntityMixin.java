package io.github.dytroc.enticingdemocracy.mixins;

import io.github.dytroc.enticingdemocracy.EnticingDemocracy;
import io.github.dytroc.enticingdemocracy.event.Events;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
    @Shadow @Final private EntityType<?> type;

    @Inject(at = @At("RETURN"), method = "getJumpVelocityMultiplier", cancellable = true)
    private void changeJumpVelocity(CallbackInfoReturnable<Float> cir) {
        if (EnticingDemocracy.hasTemporaryEvent(Events.jumpHigh)) if (type == EntityType.PLAYER) cir.setReturnValue(cir.getReturnValue() * 2.5f);
    }
}
