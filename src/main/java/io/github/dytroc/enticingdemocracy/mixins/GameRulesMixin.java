package io.github.dytroc.enticingdemocracy.mixins;

import io.github.dytroc.enticingdemocracy.EnticingDemocracy;
import io.github.dytroc.enticingdemocracy.event.Events;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRules.class)
public class GameRulesMixin {
    @Shadow @Final public static GameRules.Key<GameRules.BooleanRule> NATURAL_REGENERATION;

    @Inject(at = @At("RETURN"), method = "getBoolean", cancellable = true)
    private void onBoolean(GameRules.Key<GameRules.BooleanRule> rule, CallbackInfoReturnable<Boolean> cir) {
        if (EnticingDemocracy.hasTemporaryEvent(Events.noNaturalRegen)) {
            if (rule == NATURAL_REGENERATION) {
                cir.setReturnValue(false);
            }
        }
    }
}
