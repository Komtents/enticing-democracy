package io.github.dytroc.enticingdemocracy.event

import net.minecraft.block.Blocks
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.LinkedList
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class EventManager(server: MinecraftServer) {
    val appliedPermanentEvents = ArrayList<Event>()
    val appliedTemporaryEvents = ArrayList<Event>()

    var currentPoll: EventPoll? = null
    var remainingPollTime: Int = 0
    var intermission: Int = 20 * 45

    val votes = ConcurrentHashMap<String, Int>()

    val affectedTNTRunBlocks = HashMap<UUID, TNTRunLink>()

    inner class TNTRunLink(vararg initialElements: Pair<World, BlockPos?>) : LinkedList<Pair<World, BlockPos?>>() {
        init { addAll(initialElements) }
        override fun add(element: Pair<World, BlockPos?>): Boolean {
            if (size >= 15) poll()?.let { (world, pos) -> if (pos != null) world.setBlockState(pos, Blocks.AIR.defaultState) }
            return super.add(element)
        }
    }
}