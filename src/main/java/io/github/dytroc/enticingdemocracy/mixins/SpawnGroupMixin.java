package io.github.dytroc.enticingdemocracy.mixins;

import io.github.dytroc.enticingdemocracy.EnticingDemocracy;
import io.github.dytroc.enticingdemocracy.event.Events;
import net.minecraft.entity.SpawnGroup;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpawnGroup.class)
public class SpawnGroupMixin {
    @Shadow @Final private boolean peaceful;

    @Inject(at = @At("RETURN"), method = "getCapacity", cancellable = true)
    private void getCapacity(CallbackInfoReturnable<Integer> cir) {
        if (EnticingDemocracy.hasPermanentEvent(Events.higherSpawnCap)) if (!peaceful) cir.setReturnValue(cir.getReturnValue() * 10);
    }
}
