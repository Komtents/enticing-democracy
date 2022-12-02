package io.github.dytroc.enticingdemocracy.mixins;

import io.github.dytroc.enticingdemocracy.client.gui.VoteMediumSettingsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
class OptionsScreenMixin extends Screen {
    protected OptionsScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void drawButtons(CallbackInfo ci) {
        if (client == null) return;

        this.addDrawableChild(new ButtonWidget(
                this.width / 2 - 100, this.height / 6 + 142, 200, 20,
                Text.translatable("ui.vote_medium_settings.title"),
                btn -> client.setScreen(new VoteMediumSettingsScreen(this))
        ));


    }
}