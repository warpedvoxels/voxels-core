@file:Suppress("UNCHECKED_CAST")

package net.warpedvoxels.proxy.core

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

public typealias LifecyclePropertyInitializer<T> = suspend () -> T

public typealias LifecyclePropertyReceiver<T> = suspend (T) -> Unit

@Suppress("MemberVisibilityCanBePrivate")
public data class PluginLifecycleProperty<T>(
    val priority: Int,
    val listeners: PluginLifecyclePropertyListeners<T>,
) : Comparable<PluginLifecycleProperty<*>> {
    internal var value: T? = null

    public var isReady: Boolean = false
        internal set

    override fun compareTo(other: PluginLifecycleProperty<*>): Int =
        priority.compareTo(other.priority)
}

public data class PluginLifecyclePropertyListeners<T>(
    val proxyInitialize: LifecyclePropertyInitializer<T>,
    val proxyShutdown: LifecyclePropertyReceiver<T>
)

public class PluginLifecycleDelegatedProperty<T>(
    public val property: PluginLifecycleProperty<T>,
) : ReadOnlyProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (!this.property.isReady) {
            throw IllegalAccessException("Property not ready yet.")
        }
        return this.property.value as T
    }

    /**
     * Registers the property to the extension's lifecycle.
     */
    context(VelocityModule)
    public operator fun unaryPlus(): PluginLifecycleDelegatedProperty<T> =
        install(this@PluginLifecycleDelegatedProperty)
}

/**
 * Lazily initialized property, available once the extension is
 * enabled.
 * @param priority The priority this property would be initialized
 *                 first other than the other lazy properties.
 * @param init     Initialization hook, called when the extension is
 *                 enabled.
 * @param finalise Initialization hook, called when the extension is
 *                 disabled, turns the property inaccessible as well.
 */
public fun <T : Any> lifecycle(
    priority: Int = 0,
    init: LifecyclePropertyInitializer<T>,
    finalise: LifecyclePropertyReceiver<T>
): PluginLifecycleDelegatedProperty<T> {
    val listeners = PluginLifecyclePropertyListeners(init, finalise)
    val lifecycle = PluginLifecycleProperty(priority, listeners)
    return PluginLifecycleDelegatedProperty(lifecycle)
}

