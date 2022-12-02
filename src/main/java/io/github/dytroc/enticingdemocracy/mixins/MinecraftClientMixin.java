package io.github.dytroc.enticingdemocracy.mixins;

import io.github.dytroc.enticingdemocracy.client.EnticingDemocracyConfig;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
class MinecraftClientMixin {
    @Inject(method = "stop", at = @At("HEAD"))
    public void stop(CallbackInfo ci) {
        EnticingDemocracyConfig.save();
    }
}