package io.github.dytroc.enticingdemocracy.client

import io.github.dytroc.enticingdemocracy.EnticingDemocracy
import io.github.dytroc.enticingdemocracy.NetworkConstants
import io.github.dytroc.enticingdemocracy.client.gui.EventListOverlay
import io.github.dytroc.enticingdemocracy.client.gui.EventPollOverlay
import io.github.dytroc.enticingdemocracy.client.votes.twitch.TwitchIntegration
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.random.Random

object EnticingDemocracyClient : ClientModInitializer {

    /**
     * Runs the mod initializer on the client environment.
     */
    override fun onInitializeClient() {
        EnticingDemocracy.logger.info("Client initialized!")
        EnticingDemocracyConfig.load()
        TwitchIntegration.updateClient()

        HudRenderCallback.EVENT.register { matrixStack, _ ->
            EventListOverlay.renderEventList(matrixStack)
            EventPollOverlay.renderPollOverlay(matrixStack)
        }

        ClientPlayNetworking.registerGlobalReceiver(NetworkConstants.SOUND_PACKET_ID) { client, _, _, _ ->
            client.player?.let {
                client.soundManager.play(PositionedSoundInstance(
                    SoundEvents.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE,
                    SoundCategory.PLAYERS,
                    1f,
                    1f,
                    Random.create(),
                    it.blockPos
                ))
            }
        }
    }
}