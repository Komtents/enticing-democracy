package io.github.dytroc.enticingdemocracy

import io.github.dytroc.enticingdemocracy.event.*
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.entity.ItemEntity
import net.minecraft.predicate.entity.EntityPredicates
import net.minecraft.text.Text.literal
import net.minecraft.text.Text.translatable
import net.minecraft.util.Formatting
import net.minecraft.util.WorldSavePath
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent
import org.slf4j.LoggerFactory
import kotlin.random.Random.Default.nextInt

object EnticingDemocracy : ModInitializer {

    var manager: EventManager? = null
    val logger = LoggerFactory.getLogger("enticing_democracy")

    /**
     * Runs the mod initializer.
     */
    override fun onInitialize() {
        logger.info("Mod initialized!")
        Events.initialize()

        ServerLifecycleEvents.SERVER_STARTED.register {
            manager = EventManager(it).apply {
                val file = it.getSavePath(WorldSavePath.ROOT).resolve("enticing-democracy-events.data").toFile()
                file.createNewFile()
                val totalEvents = Event.totalEvents.values.flatten()
                file.readLines().forEach { line ->
                    val eventData = line.split("=")
                    if (eventData.size != 2) return@forEach
                    val time = eventData[1].toIntOrNull()?: return@forEach
                    val event = totalEvents.find { event -> event.id == eventData[0] }?: return@forEach
                    if (time == -1) appliedPermanentEvents.add(event.copy())
                    else appliedTemporaryEvents.add(event.copy().apply { timeProgress = time })
                }
            }
        }

        ServerLifecycleEvents.SERVER_STOPPED.register {
            manager?.let { manager ->
                val file = it.getSavePath(WorldSavePath.ROOT).resolve("enticing-democracy-events.data").toFile()
                file.createNewFile()
                file.writeText(
                    (manager.appliedTemporaryEvents + manager.appliedPermanentEvents).joinToString("\n") { event ->
                        "${event.id}=${event.timeProgress}"
                    }
                )
            }

            manager = null
        }

        ClientTickEvents.END_CLIENT_TICK

        ServerTickEvents.END_SERVER_TICK.register {
            manager?.apply {
                appliedTemporaryEvents.forEach { event ->
                    ++event.timeProgress
                }

                appliedTemporaryEvents.removeIf { event ->
                    val type = event.durationType as EventDurationType.Temporary
                    if (type.ticks == event.timeProgress) {
                        type.onEnd(this)
                        true
                    } else false
                }

                currentPoll?.let { poll ->
                    if (--remainingPollTime == 0) {
                        val selected = (poll.choices.maxBy { votes.count { (_, choice) -> poll.choices[choice] == it } }.copy()).let { event ->
                            if (event.id == "random_event") Event.totalEvents.values.flatten().filter { value ->
                                value.id != "random_event" && value !in appliedPermanentEvents && value !in appliedTemporaryEvents
                            }.random()
                            else event
                        }
                        selected.onStart(it)
                        when (selected.durationType) {
                            EventDurationType.Once -> {}
                            EventDurationType.Permanent -> appliedPermanentEvents.add(selected)
                            is EventDurationType.Temporary -> appliedTemporaryEvents.add(selected)
                        }
                        currentPoll = null
                        votes.clear()

                        it.playerManager.playerList.forEach { player ->
                            player.sendMessage(
                                literal("\n[").append(selected.display).append(literal("]:\n\n")).formatted(Formatting.DARK_GREEN)
                                    .append(translatable("event.${selected.id}.description").formatted(Formatting.GRAY))
                            )
                            ServerPlayNetworking.send(player, NetworkConstants.SOUND_PACKET_ID, PacketByteBufs.empty())
                        }

                        intermission = 20 * (nextInt(20, 150))
                    }
                }?: run {
                    if (--intermission == 0) {
                        val choices = Event.totalEvents[EventEffectType.values().random()]!!.filter { event ->
                            event !in appliedTemporaryEvents && event !in appliedPermanentEvents
                        }.shuffled().take(3)
                        remainingPollTime = 20 * 20
                        currentPoll = EventPoll(choices)

                        it.playerManager.playerList.forEach { player ->
                            ServerPlayNetworking.send(player, NetworkConstants.SOUND_PACKET_ID, PacketByteBufs.empty())
                        }
                    }
                }

                it.playerManager.playerList.forEach { player ->
                    if (hasTemporaryEvent(Events.tntRun)) {
                        val block = player.world to (player.blockPos.add(0.0, -0.2, 0.0)).takeUnless { pos -> player.world.getBlockState(pos).isAir }
                        affectedTNTRunBlocks[player.uuid]?.add(block)?: affectedTNTRunBlocks.put(player.uuid, TNTRunLink(block))
                    }

                    if (hasTemporaryEvent(Events.itemMagnet)) {
                        player.getWorld().getEntitiesByClass(
                            ItemEntity::class.java, player.boundingBox.expand(20.0), EntityPredicates.VALID_ENTITY
                        ).forEach { item -> item.onPlayerCollision(player) }
                    }
                }
            }
        }

        ServerPlayNetworking.registerGlobalReceiver(NetworkConstants.VOTE_PACKET_ID) { _, _, _, data, _ ->
            manager?.let {
                if (manager?.currentPoll == null) return@let
                it.votes[data.readString()] = data.readInt() - 1
            }
        }
    }

    @JvmStatic
    fun hasTemporaryEvent(event: Event) = manager?.appliedTemporaryEvents?.contains(event)?: false

    @JvmStatic
    fun hasPermanentEvent(event: Event) = manager?.appliedPermanentEvents?.contains(event)?: false
}