package io.github.dytroc.enticingdemocracy.client

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader
import io.github.dytroc.enticingdemocracy.EnticingDemocracy.logger
import io.github.dytroc.enticingdemocracy.client.votes.twitch.TwitchIntegration.twitchOAuthToken
import io.github.dytroc.enticingdemocracy.client.votes.twitch.TwitchIntegration.twitchChannel
import net.fabricmc.loader.api.FabricLoader
import java.io.FileReader
import java.io.FileWriter

object EnticingDemocracyConfig {
    private val path by lazy {
        FabricLoader.getInstance().configDir.resolve("enticingdemocracy.json")
    }

    private val file by lazy {
        path.toFile().also {
            if (!it.exists()) it.createNewFile()
        }
    }

    fun load() {
        kotlin.runCatching {
            Gson().fromJson<JsonObject>(JsonReader(FileReader(file)), JsonObject::class.java)
        }.getOrNull()?.let {
            twitchOAuthToken = it["twitch_api_key"]?.asString?: ""
            twitchChannel = it["twitch_channel"]?.asString?: ""
//            twitchClientId = it["twitch_client_id"]?.asString?: ""
//            twitchClientSecret = it["twitch_client_secret"]?.asString?: ""
        }
    }

    @JvmStatic
    fun save() {
        val jsonObject = JsonObject().apply {
            add("twitch_api_key", JsonParser.parseString(twitchOAuthToken))
            add("twitch_channel", JsonParser.parseString(twitchChannel))
//            add("twitch_client_id", JsonParser.parseString(twitchClientId))
//            add("twitch_client_secret", JsonParser.parseString(twitchClientSecret))
        }

        logger.info("Saving JSON!")

        val writer = FileWriter(path.toFile())
        writer.write(GsonBuilder().setPrettyPrinting().create().toJson(jsonObject))
        writer.flush()
    }
}