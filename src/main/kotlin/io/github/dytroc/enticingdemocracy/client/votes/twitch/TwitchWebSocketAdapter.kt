package io.github.dytroc.enticingdemocracy.client.votes.twitch

import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketAdapter
import com.neovisionaries.ws.client.WebSocketFrame
import io.github.dytroc.enticingdemocracy.EnticingDemocracy
import io.github.dytroc.enticingdemocracy.EnticingDemocracy.logger
import io.github.dytroc.enticingdemocracy.NetworkConstants.VOTE_PACKET_ID
import io.github.dytroc.enticingdemocracy.client.votes.twitch.TwitchIntegration.connected
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import java.util.*

class TwitchWebSocketAdapter(
    private val oAuthToken: String, private val twitchChannel: String, private val originalId: Int, private val socket: WebSocket
) : WebSocketAdapter() {
    override fun onConnected(websocket: WebSocket?, headers: MutableMap<String, MutableList<String>>?) {
        connected = true
        logger.info("The mod has successfully connected to the Twitch IRC Server!")

//        socket.sendText("CAP REQ : twitch.tv/tags twitch.tv/commands", true)
//        socket.sendText("CAP END")
        socket.sendText("PASS oauth:$oAuthToken")
        socket.sendText("NICK twitch")
        socket.sendText("JOIN #$twitchChannel")
    }

    override fun onDisconnected(websocket: WebSocket?, serverCloseFrame: WebSocketFrame?, clientCloseFrame: WebSocketFrame?, closedByServer: Boolean) {
        connected = false
        logger.info("The mod has disconnected to the Twitch IRC Server!")

        kotlin.runCatching { socket.disconnect() }

        if (originalId == TwitchIntegration.currentId) TwitchIntegration.updateClient()
    }

    override fun onTextMessage(websocket: WebSocket, text: String) {
        if (text.isNotEmpty()) {
            val data = TwitchMessageParser.parseMessage(text)?: return
            val params = data.parameters.trimEnd()
            when (data.command.command) {
                "PRIVMSG" -> {
                    connected = true
                    params.toIntOrNull()?.let {
                        if (it in 1..3) {
                            ClientPlayNetworking.send(VOTE_PACKET_ID, PacketByteBufs.create().apply {
                                writeString("twitch_${data.source?.first}")
                                writeInt(it)
                            })
                        }
                    }
                }
                "PING" -> socket.sendText("PONG $params")
                "PART" -> {
                    logger.error("The connected channel might've banned the bot!")
                    connected = false
                    socket.disconnect()
                }
                "NOTICE" -> logger.error(when (params) {
                    "Login authentication failed" -> "Twitch authentication has failed!"
                    "You don’t have permission to perform that action" -> "Not sufficient permission!"
                    else -> return
                }).also {
                    connected = false
                    socket.sendText("PART #$twitchChannel")
                }
                else -> {}
            }
        }
    }
}