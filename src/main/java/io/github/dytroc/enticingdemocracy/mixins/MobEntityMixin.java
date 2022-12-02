package io.github.dytroc.enticingdemocracy.mixins;

import io.github.dytroc.enticingdemocracy.EnticingDemocracy;
import io.github.dytroc.enticingdemocracy.event.Events;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public class MobEntityMixin {
    @Inject(at = @At("RETURN"), method = "canTarget", cancellable = true)
    private void canTarget(EntityType<?> type, CallbackInfoReturnable<Boolean> cir) {
        if (EnticingDemocracy.hasTemporaryEvent(Events.notTargeted)) if (type == EntityType.PLAYER) cir.setReturnValue(false);
    }
}
