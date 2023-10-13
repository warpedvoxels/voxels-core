@file:JvmName("ComposeHudEntrypoint")

package net.warpedvoxels.core.compose.hud

import androidx.compose.runtime.BroadcastFrameClock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.snapshots.ObserverHandle
import androidx.compose.runtime.snapshots.Snapshot
import kotlinx.coroutines.*
import net.warpedvoxels.core.compose.ComposableScope
import net.warpedvoxels.core.compose.hud.layout.NodeApplier
import net.warpedvoxels.core.compose.hud.layout.RootNode
import net.warpedvoxels.core.compose.hud.renderer.render
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

public class VoxelsHud(
    private val updatingInterval: Duration = 50.milliseconds,
    private val content: @Composable ComposableScope.() -> Unit
): ComposableScope {
    private var hasFrameWaiters = false

    private val clock = BroadcastFrameClock { hasFrameWaiters = true }
    private val compositionScope = CoroutineScope(Dispatchers.Default) + Job() + clock

    private val rootNode = RootNode()
    private val recomposer = Recomposer(coroutineContext)
    private val composition = Composition(NodeApplier(rootNode), recomposer)

    private val snapshotHandle: ObserverHandle = run {
        var applyScheduled = false
        Snapshot.registerGlobalWriteObserver {
            if (!applyScheduled) applyScheduled = true
            compositionScope.launch {
                applyScheduled = false
                Snapshot.sendApplyNotifications()
            }
        }
    }
    override val coroutineContext: CoroutineContext
        get() = compositionScope.coroutineContext

    private fun cancel() {
        recomposer.cancel()
        snapshotHandle.dispose()
        composition.dispose()
        if (compositionScope.isActive) {
            compositionScope.cancel()
        }
        if (isActive) {
            this.cancel(null)
        }
    }

    init {
        launch(start = CoroutineStart.UNDISPATCHED) {
            recomposer.runRecomposeAndApplyChanges()
        }
        launch {
            setContent(content)
            while (true) {
                ensureActive()
                if (hasFrameWaiters) {
                    hasFrameWaiters = false
                    clock.sendFrame(0L) // Frame time value is not used by Compose runtime.
                    rootNode.render()
                }
                delay(updatingInterval)
            }
        }
        coroutineContext[Job]!!.invokeOnCompletion { cancel() }
    }

    override fun setContent(content: @Composable ComposableScope.() -> Unit) {
        hasFrameWaiters = true
        composition.setContent {
            content()
        }
    }
}
