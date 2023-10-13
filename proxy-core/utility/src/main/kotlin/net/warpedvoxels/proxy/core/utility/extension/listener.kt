@file:JvmName("VelocityListenerExtension")

package net.warpedvoxels.proxy.core.utility.extension

import com.velocitypowered.api.event.AwaitingEventExecutor
import com.velocitypowered.api.event.PostOrder
import com.velocitypowered.api.event.ResultedEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.RENDEZVOUS
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onStart
import net.warpedvoxels.proxy.core.UsesModule
import net.warpedvoxels.proxy.core.VelocityModule
import net.warpedvoxels.proxy.core.coroutines.suspendingEventTask
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * A DSL marker for event listening-related functions.
 */
@DslMarker
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
internal annotation class ProxyEventListenerDslMarker

/** A Velocity event listener that holds a [VelocityModule]. */
public interface VelocityListener : UsesModule

/**
 * Represents a callback function used to handle events in Velocity. This type alias
 * allows you to run custom code when an event is triggered, while still maintaining
 * access to the related [VelocityModule].
 *
 * @param T The type of the event that will be handled by this callback.
 * @receiver The instance of the event being triggered. Within the callback, you can
 *     access properties and functions of the event as if you were inside an extension
 *     function on the event type.
 */
public typealias EventListenerCallback<T> = suspend T.(module: VelocityModule) -> Unit

/**
 * Kotlin DSL for event listening on [VelocityModule]s.
 * Uses the given [listener] as the object to attribute the listening
 * to instead of creating a new one for the sole purpose of listening
 * to a single event.
 *
 * @param listener The object to attribute the listening to.
 * @param order The listener post order in execution.
 * @param callback The code to be executed when the event is triggered.
 *
 * ## Examples
 *
 * ```kotlin
 * fun VelocityExtension.chatFeedback() = listen<PlayerChatEvent>(
 *    listener = object: VelocityListener {
 *      override val module: VelocityExtension = this@playerJoinMessage
 *    },
 *    order = PostOrder.FIRST,
 *    coroutineContext = { EmptyCoroutineContext },
 *    callback = {
 *      player.sendMessage(Component.text("Hello, ${player.username}!"))
 *    }
 * )
 */
@ProxyEventListenerDslMarker
public inline fun <reified T : Any> VelocityModule.listen(
    listener: VelocityListener,
    order: PostOrder = PostOrder.NORMAL,
    crossinline coroutineContext: T.() -> CoroutineContext = { EmptyCoroutineContext },
    noinline callback: EventListenerCallback<T>,
): Unit = proxyServer.eventManager.register(listener,
    T::class.java, order, AwaitingEventExecutor {
        suspendingEventTask(it.coroutineContext()) { it.callback(this) }
    })

/**
 * Kotlin DSL for event listening on [VelocityModule]s.
 * Creates a new [VelocityListener] *only* for the purpose of attributing this
 * single event listening to it.
 *
 * @param order The listener post order in execution.
 * @param callback The code to be executed when the event is triggered.
 * @receiver The [VelocityModule] to attribute the listening to.
 *
 * ## Examples
 *
 * ```kotlin
 * fun VelocityExtension.chatFeedback() = listen<PlayerChatEvent>(
 *   order = PostOrder.FIRST,
 *   coroutineContext = { EmptyCoroutineContext },
 *   callback = {
 *     player.sendMessage(Component.text("Hello, ${player.username}!"))
 *   }
 * )
 */
@ProxyEventListenerDslMarker
public inline fun <reified T : ResultedEvent<*>> VelocityModule.listen(
    order: PostOrder = PostOrder.NORMAL,
    crossinline coroutineContext: T.() -> CoroutineContext = { EmptyCoroutineContext },
    noinline callback: EventListenerCallback<T>,
): Unit = listen(object : VelocityListener {
    override val module: VelocityModule = this@listen
}, order, coroutineContext, callback)

/**
 * Kotlin DSL for event listening on [VelocityModule]s.
 *
 * @param order The listener post order in execution.
 * @param callback The code to be executed when the event is triggered.
 *
 * ## Examples
 *
 * ```kotlin
 * fun VelocityExtension.chatFeedback() = listen<PlayerChatEvent>(
 *    listener = object: VelocityListener {
 *      override val module: VelocityExtension = this@playerJoinMessage
 *    },
 *    order = PostOrder.FIRST,
 *    coroutineContext = { EmptyCoroutineContext },
 *    callback = {
 *      player.sendMessage(Component.text("Hello, ${player.username}!"))
 *    }
 * )
 */
public inline fun <reified T: ResultedEvent<*>> VelocityListener.listen(
    order: PostOrder = PostOrder.NORMAL,
    crossinline coroutineContext: T.() -> CoroutineContext = { EmptyCoroutineContext },
    noinline callback: EventListenerCallback<T>,
): Unit = module.listen(this, order, coroutineContext, callback)

/**
 * Registers a listener.
 * @param listener The listener to register.
 */
public inline fun VelocityModule.listen(listener: VelocityListener): Unit =
    proxyServer.eventManager.register(plugin, listener)

/** Unregisters a listener. */
public inline fun VelocityListener.unregister(): Unit =
    module.proxyServer.eventManager.unregisterListener(module.plugin, this)

/** Used to filter events published to a [Channel]. */
public typealias EventPublishingPredicate<E> = suspend E.() -> Boolean

/**
 * Consumes a cold stream of Velocity events as a [Flow] of [E]s from the given
 * channel.
 *
 * @param order The listener post order in execution.
 * @param coroutineContext The coroutine context to use for dispatching coroutines.
 * @param listener The listener the listening will be attributed to.
 * @param if The condition to have events published to the target [channel].
 * @param channel The channel to consume and publish from.
 * @param E The type of the events to listen to.
 */
public inline fun <reified E: ResultedEvent<*>> VelocityModule.eventFlow(
    order: PostOrder = PostOrder.NORMAL,
    crossinline coroutineContext: E.() -> CoroutineContext = { EmptyCoroutineContext },
    crossinline `if`: EventPublishingPredicate<E> = { true },
    listener: VelocityListener = object : VelocityListener {
        override val module: VelocityModule = this@eventFlow
    },
    channel: Channel<E> = Channel(RENDEZVOUS),
): Flow<E> = channel.consumeAsFlow().onStart {
    listener.listen<E>(order, coroutineContext) {
        if (`if`(this)) channel.send(this)
    }
}.also {
    channel.invokeOnClose {
        listener.unregister()
    }
}