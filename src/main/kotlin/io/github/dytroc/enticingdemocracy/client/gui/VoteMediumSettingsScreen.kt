package io.github.dytroc.enticingdemocracy.client.gui

import io.github.dytroc.enticingdemocracy.client.EnticingDemocracyConfig.save
import io.github.dytroc.enticingdemocracy.client.votes.twitch.TwitchIntegration
import io.github.dytroc.enticingdemocracy.client.votes.twitch.TwitchIntegration.twitchOAuthToken
import io.github.dytroc.enticingdemocracy.client.votes.twitch.TwitchIntegration.twitchChannel
import io.github.dytroc.enticingdemocracy.client.votes.twitch.TwitchIntegration.updateClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text.literal
import net.minecraft.text.Text.translatable
import net.minecraft.util.Formatting
import net.minecraft.util.math.MathHelper

class VoteMediumSettingsScreen(private val parent: Screen) : Screen(translatable("ui.vote_medium_settings.title")) {
    private val margin = 60
    private val labels = listOf(
        translatable("ui.input.twitch_token"),
        translatable("ui.input.twitch_channel"),
    )
    private val inputMargin by lazy {
        labels.maxOf { textRenderer.getWidth(it) } + 15
    }
    private val totalWidth by lazy {
        inputMargin + 200
    }

    private val barXOffset by lazy {
        (inputMargin / 2) + (200 / -2)
    }

    override fun init() {
        val twitchOAuthInput = TextFieldWidget(
            textRenderer,
            (width / 2) + barXOffset,
            margin + 30,
            200,
            20,
            labels[0]
        ).apply {
            setMaxLength(64)
            setRenderTextProvider { input, _ -> literal(input).formatted(Formatting.OBFUSCATED).asOrderedText() }
            text = twitchOAuthToken
        }.also {
            addDrawableChild(it)
        }

        val twitchChannelInput = TextFieldWidget(
            textRenderer,
            (width / 2) + barXOffset,
            margin + 60,
            200,
            20,
            labels[1]
        ).apply {
            text = twitchChannel
        }.also {
            addDrawableChild(it)
        }

        addDrawableChild(ButtonWidget(
            width / 2 - 100, margin + 150, 200, 20, translatable("ui.button.apply_changes")
        ) {
            twitchOAuthToken = twitchOAuthInput.text
            twitchChannel = twitchChannelInput.text

            TwitchIntegration.currentId += 1
            updateClient()
            save()

            close()
        })
    }

    private val white = MathHelper.packRgb(255, 255, 255)
    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(matrices)

        drawCenteredText(matrices, textRenderer, title, width / 2, 15, white)
        drawCenteredText(matrices, textRenderer, translatable("ui.vote_medium_settings.twitch"), width / 2, margin, white)

        labels.forEachIndexed { index, label ->
            drawTextWithShadow(
                matrices,
                textRenderer,
                label.append(literal(":")),
                (width / 2) - (totalWidth / 2),
                margin + (30 * (index + 1)) + textRenderer.fontHeight - 2,
                white
            )
        }
        super.render(matrices, mouseX, mouseY, delta)
    }

    override fun close() {
        client?.setScreen(parent)
    }
}