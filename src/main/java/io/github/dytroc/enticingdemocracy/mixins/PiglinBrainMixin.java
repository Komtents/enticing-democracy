package io.github.dytroc.enticingdemocracy.mixins;

import io.github.dytroc.enticingdemocracy.EnticingDemocracy;
import io.github.dytroc.enticingdemocracy.event.Events;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PiglinBrain.class)
public class PiglinBrainMixin {
    @Inject(at = @At("HEAD"), method = "doBarter", cancellable = true)
    private static void doBarter(PiglinEntity piglin, List<ItemStack> items, CallbackInfo ci) {
        if (EnticingDemocracy.hasPermanentEvent(Events.noTrade)) ci.cancel();
    }
}
