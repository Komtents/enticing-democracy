package io.github.dytroc.enticingdemocracy.client.gui

import io.github.dytroc.enticingdemocracy.EnticingDemocracy.manager
import io.github.dytroc.enticingdemocracy.event.EventDurationType
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.ColorHelper
import net.minecraft.util.math.MathHelper
import kotlin.math.ceil
import kotlin.math.roundToInt

object EventListOverlay {
    private const val eventListMargin = 40

    private val client = MinecraftClient.getInstance()
    private val fontRenderer = client.textRenderer
    private val color = MathHelper.packRgb(255, 255, 255)
    private val rectColor = ColorHelper.Argb.getArgb(255, 255, 255, 255)

    fun renderEventList(stack: MatrixStack) = manager?.let { manager ->
        (manager.appliedPermanentEvents + manager.appliedTemporaryEvents).forEachIndexed { index, it ->
            val textWidth = fontRenderer.getWidth(it.display)
            DrawableHelper.drawTextWithShadow(
                stack,
                fontRenderer,
                it.display,
                client.window.scaledWidth - textWidth - eventListMargin,
                client.window.scaledHeight - (15 + index * (fontRenderer.fontHeight + 5)),
                color
            )

            val totalWidth = eventListMargin - (5 * 2)
            val type = it.durationType

            if (type is EventDurationType.Temporary) {
                DrawableHelper.fill(
                    stack,
                    client.window.scaledWidth - 5 - totalWidth,
                    client.window.scaledHeight - (15 + index * (fontRenderer.fontHeight + 5) - 1 - (fontRenderer.fontHeight / 2) + 3),
                    client.window.scaledWidth - 5 - (totalWidth * (it.timeProgress.toDouble() / type.ticks)).toInt(),
                    client.window.scaledHeight - (15 + index * (fontRenderer.fontHeight + 5) - 1 - (fontRenderer.fontHeight / 2) - 3),
                    rectColor
                )
            }
        }
    }
}