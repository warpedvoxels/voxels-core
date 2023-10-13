package net.warpedvoxels.proxy.core.coroutines

import com.velocitypowered.api.scheduler.ScheduledTask
import kotlinx.coroutines.*
import net.warpedvoxels.proxy.core.UsesModule
import net.warpedvoxels.proxy.core.VelocityModule
import java.util.concurrent.TimeUnit
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
public class VelocityDispatcher(
    override val module: VelocityModule
) : UsesModule, Delay, CoroutineDispatcher() {
    private fun runTask(callback: Runnable): ScheduledTask =
        module.proxyServer.scheduler.buildTask(module.plugin, callback).schedule()

    private fun runTaskLater(delay: Long, callback: Runnable): ScheduledTask =
        module.proxyServer.scheduler.buildTask(module.plugin, callback)
            .delay(delay, TimeUnit.MILLISECONDS)
            .schedule()

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        runTask(block)
    }

    override fun scheduleResumeAfterDelay(
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
