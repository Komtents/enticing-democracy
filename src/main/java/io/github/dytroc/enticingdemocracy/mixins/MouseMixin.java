package io.github.dytroc.enticingdemocracy.mixins;

import io.github.dytroc.enticingdemocracy.EnticingDemocracy;
import io.github.dytroc.enticingdemocracy.event.Events;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(Mouse.class)
public class MouseMixin {
    @Shadow private double cursorDeltaX;

    @Shadow private double cursorDeltaY;

    @Inject(at = @At("HEAD"), method = "updateMouse")
    private void onUpdateMouse(CallbackInfo ci) {
        if (EnticingDemocracy.hasTemporaryEvent(Events.intenseMouseShaking)) {
            Random random = new Random();
            this.cursorDeltaX += random.nextDouble(30.0, 75.0) * (random.nextBoolean() ? 1 : -1);
            this.cursorDeltaY += random.nextDouble(0.1, 5.0) * (random.nextBoolean() ? 1 : -1);
        }
    }
}
