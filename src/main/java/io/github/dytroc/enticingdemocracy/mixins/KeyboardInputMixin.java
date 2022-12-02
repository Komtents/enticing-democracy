package io.github.dytroc.enticingdemocracy.mixins;

import io.github.dytroc.enticingdemocracy.EnticingDemocracy;
import io.github.dytroc.enticingdemocracy.event.Events;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin extends Input {
    @Inject(at = @At("TAIL"), method = "tick")
    private void tick(boolean slowDown, float f, CallbackInfo ci) {
        if (EnticingDemocracy.hasTemporaryEvent(Events.movingIncapability)) {
            this.movementForward = 0;
            this.movementSideways = 0;
            this.jumping = false;
            return;
        } else if (EnticingDemocracy.hasTemporaryEvent(Events.weirdControls)) {
            if (pressingForward || pressingBack || pressingLeft || pressingRight) {
                Random random = new Random();
                if (random.nextDouble() < 0.25) {
                    this.movementForward = random.nextInt(-1, 2);
                    this.movementSideways = random.nextInt(-1, 2);

                    if (slowDown) {
                        this.movementSideways *= f;
                        this.movementForward *= f;
                    }
                }
            }
        }

        if (EnticingDemocracy.hasTemporaryEvent(Events.invertedControls)) {
            this.movementSideways *= -1;
            this.movementForward *= -1;
        }
    }
}
