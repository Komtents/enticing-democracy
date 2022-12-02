package io.github.dytroc.enticingdemocracy.event

import net.minecraft.server.MinecraftServer
import net.minecraft.text.Text

interface Event {
    val display: Text
    get() = Text.translatable("event.${id}.display")

    val id: String
    val durationType: EventDurationType
    val onStart: (MinecraftServer) -> Unit
    val effectType: EventEffectType
    var timeProgress: Int

    fun copy() = object : Event {
        override val id = this@Event.id
        override val durationType = this@Event.durationType
        override val onStart = this@Event.onStart
        override val effectType = this@Event.effectType
        override var timeProgress = if (durationType is EventDurationType.Temporary) 0 else -1
    }

    companion object {
        val totalEvents = HashMap<EventEffectType, Set<Event>>()

        fun create(
            id: String,
            durationType: EventDurationType,
            effectType: EventEffectType,
            onStart: (MinecraftServer) -> Unit = {},
        ): Event = object : Event {
            override val id = id
            override val durationType = durationType
            override val onStart = onStart
            override val effectType = effectType
            override var timeProgress = if (durationType is EventDurationType.Temporary) 0 else -1

            override fun equals(other: Any?) = (other as? Event)?.id == id
        }.also {
            totalEvents[effectType] = totalEvents[effectType]?.plus(it)?: setOf(it)
        }
    }
}