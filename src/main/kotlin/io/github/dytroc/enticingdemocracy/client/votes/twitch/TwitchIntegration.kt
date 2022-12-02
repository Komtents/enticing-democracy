package io.github.dytroc.enticingdemocracy.client.votes.twitch

import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

object TwitchIntegration {
    private val isTwitchEnabled: Boolean
    get() = twitchChannel.isNotBlank() && twitchOAuthToken.isNotBlank()

    private val factory = WebSocketFactory().apply {
        sslContext = try {
            SSLContext.getInstance("TLS").apply {
                init(null, arrayOf(object : X509TrustManager {
                    override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) {}

                    override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) {}

                    override fun getAcceptedIssuers(): Array<X509Certificate>? {
                        return null
                    }
                }), null)
            }
        } catch (exception: Exception) {
            throw RuntimeException("Failed to initialize SSLContext", exception)
        }

    }

    var client: WebSocket? = null

    var twitchChannel = ""
    var twitchOAuthToken = ""
    var currentId = 0
    var connected = false

    fun updateClient() {
        client = if (isTwitchEnabled) {
            connected = true
            client?.disconnect()
            factory.createSocket("wss://irc-ws.chat.twitch.tv:443".format()).apply {
                addListener(TwitchWebSocketAdapter(twitchOAuthToken, twitchChannel, currentId, this))
            }.connect()
        } else {
            client?.disconnect()
            null
        }
    }
}