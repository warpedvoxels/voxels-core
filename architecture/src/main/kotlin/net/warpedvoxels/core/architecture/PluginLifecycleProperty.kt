@file:Suppress("UNCHECKED_CAST")

package net.warpedvoxels.core.architecture

import kotlinx.coroutines.CoroutineScope
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

public typealias LifecycleInitialStateListener = suspend () -> Unit

public typealias LifecyclePropertyInitializer<T> = suspend CoroutineScope.() -> T

public typealias LifecyclePropertyReceiver<T> = suspend (T) -> Unit

@DslMarker
@Target(AnnotationTarget.FUNCTION)
public annotation class LifecycleDsl

/**
 * Holds the [priority] and listening callbacks of a lifecycle property.
 */
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

/**
 * A data class that holds the initialization hooks for a
 * [PluginLifecycleProperty].
 */
public data class PluginLifecyclePropertyListeners<T>(
    val load: LifecycleInitialStateListener?,
    val enable: LifecyclePropertyInitializer<T>,
    val disable: LifecyclePropertyReceiver<T>
)

/**
 * Read-only Kotlin delegated property for [PluginLifecycleProperty],
 * throws an [IllegalAccessException] until the property is ready to use.
 */
public class PluginLifecycleDelegatedProperty<T>(
    override val plugin: VoxelsPlugin,
    public val property: PluginLifecycleProperty<T>,
) : ReadOnlyProperty<Any?, T>, UsesPlugin {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (!this.property.isReady) {
            throw IllegalAccessException("Property not ready yet.")
        }
        return this.property.value as T
    }

    /**
     * Registers the property to the extension's lifecycle.
     */
    context(VoxelsPlugin)
    public operator fun unaryPlus(): PluginLifecycleDelegatedProperty<T> =
        install(this@PluginLifecycleDelegatedProperty)
}

/**
 * Lazily initialised property, available once the extension is
 * enabled.
 * @param priority The priority this property would be initialised
 *     first other than the other lazy properties.
 * @param enable Initialisation hook, called when the extension is
 *     enabled.
 * @param disable Finalisation hook, called when the extension is
 *     disabled, turns the property inaccessible as well.
 */
@LifecycleDsl
public fun <T : Any> VoxelsPlugin.lifecycle(
    priority: Int = 0,
    load: LifecycleInitialStateListener? = null,
    disable: LifecyclePropertyReceiver<T> = {},
    enable: LifecyclePropertyInitializer<T>,
): PluginLifecycleDelegatedProperty<T> {
    val listeners = PluginLifecyclePropertyListeners(load, enable, disable)
    val lifecycle = PluginLifecycleProperty(priority, listeners)
    return PluginLifecycleDelegatedProperty(this, lifecycle)
}
