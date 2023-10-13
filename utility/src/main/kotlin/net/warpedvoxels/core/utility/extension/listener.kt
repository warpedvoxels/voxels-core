package net.warpedvoxels.core.utility.extension

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import net.warpedvoxels.core.architecture.VoxelsPlugin
import net.warpedvoxels.core.architecture.UsesPlugin
import net.warpedvoxels.core.architecture.dispatchers
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import kotlin.coroutines.CoroutineContext

/**
 * A DSL marker for event listening-related functions.
 */
@DslMarker
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
internal annotation class EventListenerDslMarker

/** A Bukkit [Listener] that holds a [VoxelsPlugin]. */
public interface PaperEventListener : Listener, UsesPlugin

/**
 * Represents a callback function used to handle events in Bukkit. This type alias
 * allows you to run custom code when an event is triggered. The receiver event
 * is passed as the context, and the [PaperEventListener] is passed as the receiver.
 * This combination allows you to conveniently access both the [VoxelsPlugin],
 * listener, and relevant event properties and extensions within a single scope.
 *
 * @param T The type of the event that will be handled by this callback.
 * @receiver The instance of the event being triggered. Within the callback, you can
 *     access properties and functions of the event as if you were inside an extension
 *     function on the event type.
 */
public typealias EventListenerCallback<T> = T.(listener: PaperEventListener) -> Unit

/**
 * Kotlin DSL for event listening on [VoxelsPlugin]s.
 * Uses the given [listener] as the object to attribute the listening
 * to instead of creating a new one for the sole purpose of listening
 * to a single event.
 *
 * @param listener The object to attribute the listening to.
 * @param priority The listener priority in execution.
 * @param ignoreIfCancelled Whether this listener should be triggered
 *     even if the target event is cancelled.
 * @param callback The code to be executed when the event is
 *     triggered.
 *
 * ## Examples
 *
 * ```kotlin
 * fun VoxelPlugin.playerJoinMessage() = listen<PlayerJoinEvent>(
 *     listener = object: PaperEventListener {
 *         override val extension: VoxelPlugin = this@playerJoinMessage
 *     },
 *     priority = EventPriority.NORMAL,
 *     ignoreIfCancelled = true
 * ) {
 *     joinMessage = "${ChatColor.GREEN}Welcome to the server, ${player.name}!"
 * }
 * ```
 */
@EventListenerDslMarker
public inline fun <reified E : Event> VoxelsPlugin.listen(
    listener: PaperEventListener,
    priority: EventPriority = EventPriority.NORMAL,
    ignoreIfCancelled: Boolean = true,
    crossinline callback: EventListenerCallback<E>
): PaperEventListener = listener.also {
    server.pluginManager.registerEvent(
        E::class.java,
        listener,
        priority,
        { _, event ->
            if (E::class.java.isInstance(event)) (event as E).callback(listener)
        },
        this,
        ignoreIfCancelled
    )
}

/**
 * Kotlin DSL for event listening on [VoxelsPlugin]s.
 * Uses the current listener as the object to attribute the listening to.
 *
 * @param priority          The listener priority in execution.
 * @param ignoreIfCancelled Whether this listener should be triggered
 *                          even if the target event is cancelled.
 * @param callback          The code to be executed when the event is
 *                          triggered.
 *
 * ## Examples
 *
 * ```kotlin
 * fun PaperEventListener.playerJoinMessage() = listen<PlayerJoinEvent>(
 *    priority = EventPriority.NORMAL, ignoreIfCancelled = true
 * ) {
 *     joinMessage = "${ChatColor.GREEN}Welcome to the server, ${player.name}!"
 * }
 * ```
 */
@EventListenerDslMarker
public inline fun <reified E : Event> PaperEventListener.listen(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreIfCancelled: Boolean = true,
    crossinline callback: EventListenerCallback<E>
): PaperEventListener = plugin.listen(this, priority, ignoreIfCancelled, callback)

/**
 * Kotlin DSL for event listening on [VoxelsPlugin]s.
 * Creates a [PaperEventListener] that will be *only* used to listen to
 * the target event.
 *
 * @param priority The listener priority in execution.
 * @param ignoreIfCancelled Whether this listener should be triggered
 *     even if the target event is cancelled.
 * @param block The code to be executed when the event is
 *     triggered.
 *
 * ## Examples
 *
 * ```kotlin
 * fun VoxelPlugin.playerJoinMessage() = listen<PlayerJoinEvent>(
 *     priority = EventPriority.NORMAL, ignoreIfCancelled = true
 * ) {
 *     joinMessage = "${ChatColor.GREEN}Welcome to the server, ${player.name}!"
 * }
 * ```
 */
@EventListenerDslMarker
public inline fun <reified E : Event> VoxelsPlugin.listen(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreIfCancelled: Boolean = true,
    crossinline block: EventListenerCallback<E>
): PaperEventListener = listen(object : PaperEventListener {
    override val plugin: VoxelsPlugin = this@listen
}, priority, ignoreIfCancelled, block)

/** Used to filter events published to a [Channel]. */
public typealias EventPublishingPredicate<E> = E.() -> Boolean

/**
 * Consumes a cold stream of Bukkit events as a [Flow] of [E]s from
 * the given [channel].
 *
 * @param priority          The listener priority in execution.
 * @param ignoreIfCancelled Whether this listener should be triggered
 *                          even if the target event is cancelled.
 * @param context           The [CoroutineContext] to use for
 *                          dispatching coroutines.
 * @param if                The condition to have events published to
 *                          the target [channel].
 * @param listener          The object to attribute the listening to.
 * @param channel           The channel that will have events published.
 * @param E                 The type of the events to listen to.

 * ## Examples
 *
 * ```kotlin
 * suspend fun VoxelPlugin.firstJoinMessage() {
 *     val flow: Flow<PlayerJoinEvent> = eventFlow(`if` = { !player.hasPlayedBefore() })
 *     flow.onEach { it.joinMessage = "Welcome to the server, ${it.player.name}!" }
 *         .collect()
 * }
 */
public inline fun <reified E : Event> VoxelsPlugin.eventFlow(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreIfCancelled: Boolean = true,
    crossinline `if`: EventPublishingPredicate<E> = { true },
    context: CoroutineContext = dispatchers.async,
    listener: PaperEventListener = object : PaperEventListener {
        override val plugin: VoxelsPlugin = this@eventFlow
    },
    channel: Channel<E> = Channel(Channel.RENDEZVOUS)
): Flow<E> = channel.consumeAsFlow().onStart {
    listener.listen<E>(priority, ignoreIfCancelled) {
        if (`if`()) {
            launch(context) {
                channel.send(this@listen)
            }
        }
    }
}.also {
    channel.invokeOnClose {
        listener.unregister()
    }
}

/** Stops a listener from receiving new events. */
public fun Listener.unregister(): Unit =
    HandlerList.unregisterAll(this)

/**
 * A DSL scope for registering event listeners.
 */
@JvmInline
@EventListenerDslMarker
public value class BukkitListenerRegisteringScope(override val plugin: VoxelsPlugin): UsesPlugin {
    /** DSL accessor. */
    @EventListenerDslMarker
    public operator fun <R> invoke(block: BukkitListenerRegisteringScope.() -> R): R =
        block()

    /**
     * Plus operator prefix for registering an event listener.
     *
     * ## Examples
     *
     * ```kotlin
     * extension.listeners { +PlayerJoinListener() }
     * ```
     */
    public operator fun Listener.unaryPlus() {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    /**
     * Registers a listener.
     *
     * ## Examples
     *
     * ```kotlin
     * extension.listeners { install(PlayerJoinListener()) }
     */
    public fun install(vararg listener: Listener): Unit = listener.forEach { +it }
}

/**
 * A DSL scope for registering event listeners.
 */
public inline val VoxelsPlugin.listeners: BukkitListenerRegisteringScope
    get() = BukkitListenerRegisteringScope(this)