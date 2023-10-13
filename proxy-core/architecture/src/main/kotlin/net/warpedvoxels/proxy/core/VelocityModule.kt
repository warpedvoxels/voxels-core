package net.warpedvoxels.proxy.core

import com.velocitypowered.api.event.EventManager
import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.proxy.ProxyServer
import kotlinx.coroutines.*
import net.warpedvoxels.proxy.core.coroutines.VelocityDispatcher
import net.warpedvoxels.proxy.core.coroutines.registerCoroutineContinuationAdapter
import org.slf4j.Logger
import kotlin.coroutines.CoroutineContext

/**
 * A nifty extension for Velocity plugins that allows for the use of coroutines,
 * lifecycle management and overall use of the **voxels-core** library.
 */
public class VelocityModule(
    public val namespace: String,
    public val plugin: Any,
    public val logger: Logger,
    public val pluginContainer: PluginContainer,
    public val eventManager: EventManager,
    public val proxyServer: ProxyServer,
) : CoroutineScope {
    private val job: Job = SupervisorJob()
    override val coroutineContext: CoroutineContext by lazy {
        job + VelocityDispatcher(this) + CoroutineName(namespace)
    }

    init {
        registerCoroutineContinuationAdapter()
    }

    internal val lifecycles: MutableSet<PluginLifecycleProperty<Any?>> =
        mutableSetOf()
    private inline val sortedLifecycles: List<PluginLifecycleProperty<Any?>>
        get() = lifecycles.sortedBy { it.priority }

    public fun init(block: suspend VelocityModule.() -> Unit) {
        launch {
            sortedLifecycles.forEach {
                it.value = it.listeners.proxyInitialize()
                it.isReady = true
            }
            block()
        }
    }

    public fun finalise(block: suspend VelocityModule.() -> Unit) {
        launch {
            sortedLifecycles.forEach {
                it.listeners.proxyShutdown(it.value)
                it.isReady = false
            }
            block()
            job.cancel()
        }
    }

    /**
     * Registers a property to the extension's lifecycle.
     */
    @Suppress("UNCHECKED_CAST")
    public fun <T: Any?> install(lifecycle: PluginLifecycleDelegatedProperty<T>): PluginLifecycleDelegatedProperty<T> =
        lifecycle.also {
            lifecycles.add(lifecycle.property as PluginLifecycleProperty<Any?>)
        }
}