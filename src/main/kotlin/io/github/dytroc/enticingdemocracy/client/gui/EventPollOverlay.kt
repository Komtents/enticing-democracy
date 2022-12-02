package io.github.dytroc.enticingdemocracy.client.gui

import io.github.dytroc.enticingdemocracy.EnticingDemocracy
import io.github.dytroc.enticingdemocracy.EnticingDemocracy.logger
import io.github.dytroc.enticingdemocracy.EnticingDemocracy.manager
import io.github.dytroc.enticingdemocracy.client.votes.twitch.TwitchIntegration
import io.github.dytroc.enticingdemocracy.client.votes.twitch.TwitchIntegration.connected
import io.github.dytroc.enticingdemocracy.event.Event
import io.github.dytroc.enticingdemocracy.event.EventManager
import io.github.dytroc.enticingdemocracy.event.Events
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Style
import net.minecraft.text.Text.literal
import net.minecraft.text.Text.translatable
import net.minecraft.util.math.ColorHelper
import net.minecraft.util.math.MathHelper
import kotlin.math.ceil
import kotlin.math.roundToInt

object EventPollOverlay {
    private const val eventPollMargin = 10

    private val client = MinecraftClient.getInstance()
    private val fontRenderer = client.textRenderer
    private fun color(opacity: Double) = ColorHelper.Argb.getArgb((255 * opacity).roundToInt(), 255, 255, 255)

    fun renderPollOverlay(stack: MatrixStack) = manager?.let { manager ->
        if (!connected) DrawableHelper.drawTextWithShadow(
            stack,
            fontRenderer,
            translatable("ui.poll.client_not_connected"),
            client.window.scaledWidth - eventPollMargin - fontRenderer.getWidth(translatable("ui.poll.client_not_connected")),
            eventPollMargin,
            MathHelper.packRgb(255, 0, 0)
        )

        manager.currentPoll?.let { poll ->
            val opacity = when (manager.remainingPollTime) {
                in ((20 * 20 - 10)..(20 * 20)) -> -((manager.remainingPollTime / 10.0) - 40)
                else -> 1.0
            }.coerceAtLeast(0.025)

            DrawableHelper.drawTextWithShadow(
                stack,
                fontRenderer,
                translatable("ui.poll.remaining_time", ceil(manager.remainingPollTime / 20.0).toInt()),
                eventPollMargin,
                eventPollMargin,
                color(opacity)
            )

            DrawableHelper.fill(
                stack,
                eventPollMargin,
                eventPollMargin + 15,
                eventPollMargin + 175 + fontRenderer.getWidth("100%") + 5,
                eventPollMargin + 36 + (2 * 30) + 10 + 5,
                optionEmptyColor(opacity)
            )

            manager.renderPollChoice(stack, poll.choices, opacity)
        }
    }

    private fun optionEmptyColor(opacity: Double) = ColorHelper.Argb.getArgb((120 * opacity).toInt(), 20, 20, 25)
    private fun optionFilledColor(opacity: Double) = ColorHelper.Argb.getArgb((160 * opacity).toInt(), 255, 93, 0)

    private fun EventManager.renderPollChoice(stack: MatrixStack, choices: List<Event>, opacity: Double) = choices.forEachIndexed { index, choice ->
        val ratio = 0.0.takeIf { votes.isEmpty() } ?: (votes.count { it.value == index } / votes.size.toDouble())
        DrawableHelper.fill(
            stack,
            eventPollMargin + 10,
            eventPollMargin + 36 + (index * 30),
            eventPollMargin + 12 + (158 * ratio).toInt(),
            eventPollMargin + 36 + (index * 30) + 10,
            optionFilledColor(opacity)
        )

        DrawableHelper.drawTextWithShadow(
            stack,
            fontRenderer,
            literal("${index + 1}. ").append(
                choice.display.copy().apply {
                    if (EnticingDemocracy.hasTemporaryEvent(Events.hideChoiceNames)) style = Style.EMPTY.withObfuscated(true)
                }
            ),
            eventPollMargin + 5,
            eventPollMargin + 23 + (index * 30),
            color(opacity)
        )

        val text = "${(ratio * 100).roundToInt()}%"

        DrawableHelper.drawTextWithShadow(
            stack,
            fontRenderer,
            literal(text),
            eventPollMargin + 17 + (158 * ratio).toInt(),
            eventPollMargin + 37 + (index * 30),
            color(opacity)
        )
    }
}