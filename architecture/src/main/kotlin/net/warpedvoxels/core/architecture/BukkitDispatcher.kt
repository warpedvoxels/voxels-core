package net.warpedvoxels.core.architecture

import kotlinx.coroutines.*
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/** Returns a [Duration] equal to this [Int] number of ticks. */
public inline val Int.ticks: Duration
    get() = (this * 50)
        .toDuration(DurationUnit.MILLISECONDS)

/** Returns a [Duration] equal to this [Int] number of ticks. */
public inline val Long.ticks: Duration
    get() = (this * 50)
        .toDuration(DurationUnit.MILLISECONDS)

/** Return a tick count equal to this [Duration]. */
public inline val Duration.inWholeTicks: Long
    get() = this.inWholeMilliseconds / 50

@OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
public sealed class BukkitDispatcher : UsesPlugin, Delay, CoroutineDispatcher() {
    protected abstract fun runTask(callback: Runnable): BukkitTask

    protected abstract fun runTaskLater(delay: Long, callback: Runnable): BukkitTask

    final override fun scheduleResumeAfterDelay(
        timeMillis: Long,
        continuation: CancellableContinuation<Unit>
    ) {
        val task = runTaskLater(timeMillis / 50) {
            continuation.apply {
                resumeUndispatched(Unit)
            }
        }
        continuation.invokeOnCancellation {
            task.cancel()
        }
    }
}

public class SyncBukkitDispatcher(override val plugin: VoxelsPlugin) : BukkitDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        if (Bukkit.isPrimaryThread()) {
            block.run()
        }
    }

    override fun runTask(callback: Runnable): BukkitTask =
        plugin.server.scheduler.runTask(plugin, callback)

    override fun runTaskLater(delay: Long, callback: Runnable): BukkitTask =
        plugin.server.scheduler.runTaskLater(plugin, callback, delay)
}

public class AsyncBukkitDispatcher(override val plugin: VoxelsPlugin) : BukkitDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        if (!Bukkit.isPrimaryThread()) {
            runTask(block)
        }
    }

    override fun runTask(callback: Runnable): BukkitTask =
        plugin.server.scheduler.runTaskAsynchronously(plugin, callback)

    override fun runTaskLater(delay: Long, callback: Runnable): BukkitTask =
        plugin.server.scheduler.runTaskLaterAsynchronously(plugin, callback, delay)
}

/**
 * Controls what threading context should coroutines be dispatched.
 */
@JvmInline
public value class BukkitDispatchers(
    override val plugin: VoxelsPlugin
) : UsesPlugin {
    public val async: BukkitDispatcher get() = AsyncBukkitDispatcher(plugin)
    public val sync: BukkitDispatcher get() = SyncBukkitDispatcher(plugin)
}

/**
 * Controls what threading context should coroutines be dispatched.
 */
public inline val VoxelsPlugin.dispatchers: BukkitDispatchers
    get() = BukkitDispatchers(this)