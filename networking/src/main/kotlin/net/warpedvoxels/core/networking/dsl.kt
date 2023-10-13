@file:JvmName("PacketEventsDsl")

package net.warpedvoxels.core.networking

import com.github.retrooper.packetevents.PacketEventsAPI
import com.github.retrooper.packetevents.event.*
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon
import com.github.retrooper.packetevents.wrapper.PacketWrapper
import com.github.retrooper.packetevents.wrapper.login.server.WrapperLoginServerDisconnect
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@DslMarker
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
internal annotation class PacketEventsDslMarker

@JvmInline
@PacketEventsDslMarker
public value class PacketEventsRegisteringScope(private val api: PacketEventsAPI<*>) {
    /** DSL accessor. */
    @PacketEventsDslMarker
    public operator fun <R> invoke(block: PacketEventsRegisteringScope.() -> R): R =
        block()

    /**
     * Plus operator prefix for registering a packet event listener.
     * ```
     */
    public operator fun PacketListenerCommon.unaryPlus() {
        api.eventManager.registerListener(this)
    }

    /**
     * Registers a packet event listener.
     */
    public fun install(vararg listener: PacketListenerCommon): Unit = listener.forEach { +it }
}

// DSL accessor.
@PacketEventsDslMarker
public operator fun PacketEventsAPI<*>.invoke(block: PacketEventsRegisteringScope.() -> Unit): Unit {
    PacketEventsRegisteringScope(this).block()
}

public inline val discard: ProtocolPacketEvent<Any>.() -> PacketWrapper<*>?
    get() = { null }

/**
 * Listens to incoming packets.
 * @param type The type of packet to listen to.
 * @param priority The priority of this listener.
 * @param constructor The constructor for the packet wrapper.
 * @param block The block of code to run when a packet is received.
 */
public fun <E: PacketWrapper<*>?> incoming(
    type: PacketTypeCommon,
    constructor: (PacketReceiveEvent) -> E,
    priority: PacketListenerPriority = PacketListenerPriority.NORMAL,
    block: E.(event: PacketReceiveEvent) -> Unit,
): PacketListenerCommon = object : PacketListenerAbstract(priority) {
    override fun onPacketReceive(event: PacketReceiveEvent) {
        if (event.packetType == type) constructor(event).block(event)
    }
}

/** Listens to outgoing packets.
 * @param type The type of packet to listen to.
 * @param priority The priority of this listener.
 * @param constructor The constructor for the packet wrapper.
 * @param block The block of code to run when a packet is sent.
 */
public fun <E: PacketWrapper<*>?> outgoing(
    type: PacketTypeCommon,
    constructor: (PacketSendEvent) -> E,
    priority: PacketListenerPriority = PacketListenerPriority.NORMAL,
    block: E.(event: PacketSendEvent) -> Unit,
): PacketListenerCommon = object : PacketListenerAbstract(priority) {
    override fun onPacketSend(event: PacketSendEvent) {
        if (event.packetType == type) constructor(event).block(event)
    }
}

/** Listens to user connections.
 * @param priority The priority of this listener.
 * @param block The block of code to run when a user connects.
 */
public fun userConnect(
    priority: PacketListenerPriority = PacketListenerPriority.NORMAL,
    block: UserConnectEvent.() -> Unit,
): PacketListenerCommon = object : PacketListenerAbstract(priority) {
    override fun onUserConnect(event: UserConnectEvent): Unit = event.block()
}

/** Listens to user logins.
 * @param priority The priority of this listener.
 * @param block The block of code to run when a user logs in.
 */
public fun userLogin(
    priority: PacketListenerPriority = PacketListenerPriority.NORMAL,
    block: UserLoginEvent.() -> Unit,
): PacketListenerCommon = object : PacketListenerAbstract(priority) {
    override fun onUserLogin(event: UserLoginEvent): Unit = event.block()
}

/** Listens to user disconnections.
 * @param priority The priority of this listener.
 * @param block The block of code to run when a user disconnects.
 */
public fun userDisconnect(
    priority: PacketListenerPriority = PacketListenerPriority.NORMAL,
    block: UserDisconnectEvent.() -> Unit,
): PacketListenerCommon = object : PacketListenerAbstract(priority) {
    override fun onUserDisconnect(event: UserDisconnectEvent): Unit = event.block()
}
