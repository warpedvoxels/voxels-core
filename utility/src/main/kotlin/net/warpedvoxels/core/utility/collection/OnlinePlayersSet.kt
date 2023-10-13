package net.warpedvoxels.core.utility.collection

import net.warpedvoxels.core.architecture.VoxelsPlugin
import net.warpedvoxels.core.utility.extension.PaperEventListener
import net.warpedvoxels.core.utility.extension.listen
import net.warpedvoxels.core.utility.extension.unregister
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent

public typealias PlayerReceiver = (Player) -> Unit

@Suppress("MemberVisibilityCanBePrivate")
public class OnlinePlayersSet(
    override val plugin: VoxelsPlugin,
    private val onRemove: PlayerReceiver = {},
) : HashSet<Player>(), PaperEventListener {
    private fun register() {
        // Only register this event once
        if (size == 1) {
            listen<PlayerQuitEvent> {
                remove(player)
            }
        } else if (isEmpty()) {
            unregister()
        }
    }

    override fun add(element: Player): Boolean =
        super.add(element).also {
            if (it) register()
        }

    override fun remove(element: Player): Boolean =
        super.remove(element).also {
            if (it) {
                onRemove(element)
                register()
            }
        }
}

/** Creates a [Set] that removes [Player]s on fly when they
 * quit the server.
 * @param hook Callback that gets executed on every player
 *             removal.
 */
public fun VoxelsPlugin.onlinePlayersSet(
    hook: PlayerReceiver = {}
): OnlinePlayersSet = OnlinePlayersSet(this, hook)
