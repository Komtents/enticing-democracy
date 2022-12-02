package io.github.dytroc.enticingdemocracy.mixins;

import io.github.dytroc.enticingdemocracy.EnticingDemocracy;
import io.github.dytroc.enticingdemocracy.event.Events;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHUDMixin {
    private static final Identifier INK_OVERLAY = new Identifier("enticing_democracy", "textures/misc/ink.png");
    @Shadow protected abstract void renderOverlay(Identifier texture, float opacity);

    @Inject(at = @At("HEAD"), method = "render", cancellable = true)
    private void onRender(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (EnticingDemocracy.hasTemporaryEvent(Events.inkOverlay)) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.options.getPerspective().isFirstPerson()) {
                renderOverlay(INK_OVERLAY, 1f);
            }
        }
    }
}
