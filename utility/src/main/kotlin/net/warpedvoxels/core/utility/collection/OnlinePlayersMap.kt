package net.warpedvoxels.core.utility.collection

import net.warpedvoxels.core.architecture.VoxelsPlugin
import net.warpedvoxels.core.utility.extension.PaperEventListener
import net.warpedvoxels.core.utility.extension.listen
import net.warpedvoxels.core.utility.extension.unregister
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent
import java.util.function.BiFunction
import java.util.function.Function

public typealias PlayerMapReceiver<V> = (Player, V) -> Unit

@Suppress("MemberVisibilityCanBePrivate")
public class OnlinePlayersMap<V>(
    override val plugin: VoxelsPlugin,
    private val onRemove: PlayerMapReceiver<V> = { _, _ -> },
) : HashMap<Player, V>(), PaperEventListener {
    private fun register() {
        // Only register this listener once
        if (size == 1) {
            listen<PlayerQuitEvent> {
                remove(player)
            }
        } else if (isEmpty()) {
            unregister()
        }
    }

    override fun put(key: Player, value: V): V? =
        super.put(key, value).also {
            if (it == null) register()
        }

    override fun remove(key: Player): V? =
        super.remove(key).also {
            if (it != null) {
                onRemove(key, it)
                register()
            }
        }

    override fun putAll(from: Map<out Player, V>): Unit =
        super.putAll(from).also {
            if(isNotEmpty()) register()
        }

    override fun clear(): Unit =
        super.clear().also {
            unregister()
        }

    override fun replace(key: Player, value: V): V? =
        super.replace(key, value).also {
            if (it != null) {
                onRemove(key, it)
                register()
            }
        }

    override fun replace(key: Player, oldValue: V, newValue: V): Boolean =
        super.replace(key, oldValue, newValue).also {
            if (it) {
                onRemove(key, oldValue)
                register()
            }
        }

    override fun remove(key: Player, value: V): Boolean =
        super.remove(key, value).also {
            if (it) {
                onRemove(key, value)
                register()
            }
        }

    override fun putIfAbsent(key: Player, value: V): V? =
        super.putIfAbsent(key, value).also {
            if (it == null) register()
        }

    override fun compute(key: Player, remappingFunction: BiFunction<in Player, in V?, out V?>): V? =
        super.compute(key, remappingFunction).also {
            if (it == null) register()
        }

    override fun computeIfAbsent(key: Player, mappingFunction: Function<in Player, out V?>): V =
        super.computeIfAbsent(key, mappingFunction).also {
            if (it == null) register()
        }

    override fun computeIfPresent(key: Player, remappingFunction: BiFunction<in Player, in V & Any, out V?>): V? =
        super.computeIfPresent(key, remappingFunction).also {
            if (it == null) register()
        }

    override fun merge(key: Player, value: V & Any, remappingFunction: BiFunction<in V & Any, in V & Any, out V?>): V? =
        super.merge(key, value, remappingFunction).also {
            if (it == null) register()
        }

    override fun replaceAll(function: BiFunction<in Player, in V, out V>): Unit =
        super.replaceAll(function).also {
            if (isNotEmpty()) register()
        }
}

/**
 * LCreates a [Map] that removes [Player]s on fly when they
 * quit the server.
 * @param hook Callback that gets executed on every player
 *             removal.
 */
public fun <V> VoxelsPlugin.onlinePlayersMap(
    hook: PlayerMapReceiver<V> = { _, _, -> }
): OnlinePlayersMap<V> = OnlinePlayersMap(this, hook)
