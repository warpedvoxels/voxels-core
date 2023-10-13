package net.warpedvoxels.core.architecture

import kotlinx.coroutines.*
import org.bukkit.plugin.java.JavaPlugin
import kotlin.coroutines.CoroutineContext
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * A nifty extension for [JavaPlugin] that allows for the use of coroutines, lifecycle
 * management and overall use of the **voxels-core** library.
 */
public abstract class VoxelsPlugin(public val namespace: String) : JavaPlugin(), CoroutineScope {
    internal val lifecycles: MutableSet<PluginLifecycleProperty<Any?>> =
        mutableSetOf()
    private inline val sortedLifecycles: List<PluginLifecycleProperty<Any?>>
        get() = lifecycles.sortedBy { it.priority }

    private val job: Job = SupervisorJob()
    override val coroutineContext: CoroutineContext =
        job + CoroutineName(namespace)

    /** Called when the plugin is loaded. */
    public open suspend fun load() {}

    /** Called when the plugin is enabled. */
    public open suspend fun enable() {}

    /** Called when the plugin is disabled. */
    public open suspend fun disable() {}

    final override fun onLoad() {
        launch(dispatchers.sync) {
            sortedLifecycles.forEach {
                it.listeners.load?.let { it() }
            }
            load()
        }
    }

    final override fun onEnable() {
        launch(dispatchers.sync) {
            sortedLifecycles.forEach {
                it.value = it.listeners.enable(this)
                it.isReady = true
            }
            enable()
        }
    }

    final override fun onDisable() {
        launch(dispatchers.sync) {
            sortedLifecycles.forEach {
                it.listeners.disable(it.value)
                it.isReady = false
            }
            disable()
        }.invokeOnCompletion {
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

    // lifecycle delegate provider
    public operator fun <T> PluginLifecycleDelegatedProperty<T>.provideDelegate(
        ref: Any?,
        prop: KProperty<*>
    ): ReadOnlyProperty<Any?, T> {
        return install(this)
    }
}