package io.github.dytroc.enticingdemocracy.client.votes.twitch

import io.github.dytroc.enticingdemocracy.EnticingDemocracy.logger

// Taken from https://dev.twitch.tv/docs/irc/example-parser
object TwitchMessageParser {
    fun parseMessage(message: String): ParsedMessage? {
        var idx = 0

        var rawSourceComponent = ""
        var rawCommandComponent = ""
        var rawParametersComponent = ""

        if (message[idx] == '@') idx = message.indexOf(' ') + 1

        if (message[idx] == ':') {
            idx += 1
            val endIdx = message.indexOf(' ', idx)
            rawSourceComponent = message.slice(idx until endIdx)
            idx = endIdx + 1
        }

        val endIdx = message.indexOf(':', idx).takeUnless { it == -1 }?: message.length

        rawCommandComponent = message.slice(idx until endIdx).trim()

        if (endIdx != message.length) {
            idx = endIdx + 1
            rawParametersComponent = message.drop(idx)
        }

        val command = parseCommand(rawCommandComponent)?: return null
        val source = parseSource(rawSourceComponent)
        val parameters = rawParametersComponent
//        if (rawParametersComponent.isNotEmpty() && rawParametersComponent[0] == '!') command = parseParameters(rawParametersComponent, command)
        return ParsedMessage(source, command, parameters)
    }

    private fun parseCommand(rawCommandComponent: String): CommandData? {
        val commandParts = rawCommandComponent.split(' ')
        return when (commandParts[0]) {
            "JOIN", "PART", "NOTICE", "CLEARCHAT", "HOSTTARGET", "PRIVMSG", "USERSTATE", "ROOMSTATE", "001" -> CommandData(commandParts[0], commandParts[1])
            "PING", "GLOBALUSERSTATE" -> CommandData(commandParts[0])
            "CAP" -> CommandData(commandParts[0], null, commandParts[2] == "ACK")
            "RECONNECT" -> {
                logger.info("The Twitch IRC server is about to terminate the connection for maintenance.")
                CommandData(commandParts[0])
            }
            "421" -> {
                logger.info("Unsupported IRC command: ${commandParts[2]}")
                null
            }
            else -> {
                logger.info("Unexpected IRC command: ${commandParts[0]}")
                null
            }
        }
    }

    private fun parseSource(rawSourceComponent: String): Pair<String?, String>? {
        if (rawSourceComponent.isEmpty()) return null
        val sourceParts = rawSourceComponent.split("!")
        return (if (sourceParts.size == 2) sourceParts[0] else null) to (if (sourceParts.size == 2) sourceParts[1] else sourceParts[0])
    }

    private fun parseParameters(rawParametersComponent: String, command: String) {
        val commandParts = rawParametersComponent.drop(1).trim()
        return
    }

    data class ParsedMessage(val source: Pair<String?, String>?, val command: CommandData, val parameters: String)
    data class CommandData(val command: String? = null, val channel: String? = null, val isCapRequestEnabled: Boolean? = null)
}