@file:JvmName("AccuratePing")

package net.warpedvoxels.core.example.listener

import com.github.retrooper.packetevents.event.PacketListenerCommon
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import net.warpedvoxels.core.architecture.VoxelsPlugin
import net.warpedvoxels.core.networking.discard
import net.warpedvoxels.core.networking.incoming
import net.warpedvoxels.core.networking.outgoing
import net.warpedvoxels.core.utility.collection.onlinePlayersMap
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue

@get:JvmName("getAccuratePingListeners")
val VoxelsPlugin.AccuratePing: Array<PacketListenerCommon> get() {
    val pending = onlinePlayersMap<Long>()
    return arrayOf(
        incoming(PacketType.Play.Client.KEEP_ALIVE, discard) {
            val player = Bukkit.getPlayer(it.user.uuid) ?: return@incoming
            val current = pending.remove(player) ?: Int.MAX_VALUE.toLong()
            val ping = System.currentTimeMillis() - current
            player.setMetadata("accurate-ping", FixedMetadataValue(this@AccuratePing, ping))
        },
        outgoing(PacketType.Play.Server.KEEP_ALIVE, discard) {
            pending[Bukkit.getPlayer(it.user.uuid) ?: return@outgoing] = System.currentTimeMillis()
        }
    )
}

val Player.accuratePing: Long
    get() =
        getMetadata("accurate-ping").firstOrNull()?.asLong() ?: ping.toLong()