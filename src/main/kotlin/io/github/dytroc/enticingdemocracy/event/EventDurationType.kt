package io.github.dytroc.enticingdemocracy.event

sealed interface EventDurationType {
    object Permanent : EventDurationType
    object Once : EventDurationType

    class Temporary(val ticks: Int, val onEnd: (EventManager) -> Unit = {}) : EventDurationType
}