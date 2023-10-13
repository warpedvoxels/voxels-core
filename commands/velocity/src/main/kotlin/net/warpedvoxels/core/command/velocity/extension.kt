@file:JvmName("VelocityExtension")

package net.warpedvoxels.core.command.velocity

import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.command.Command
import com.velocitypowered.api.command.CommandMeta
import com.velocitypowered.api.command.CommandSource
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.warpedvoxels.core.command.*
import net.warpedvoxels.proxy.core.VelocityModule

public typealias CommandContext =
        BrigadierCommandExecutionContext<CommandSource>

/**
 * Respond a command with successful feedback.
 * @param text The text component to be sent.
 */
public fun CommandContext.respond(text: String): Int =
    respond(Component.text(text))

/**
 * Respond a command with successful feedback.
 * @param text The text component to be sent.
 */
public fun CommandContext.respond(text: Component): Int = Success.also {
    source.sendMessage(text)
}

/**
 * Respond a command with unsuccessful feedback.
 * @param text The text component to be sent.
 */
public fun CommandContext.fail(text: String): Nothing =
    fail(Component.text(text))

/**
 * Respond a command with unsuccessful feedback.
 * @param component The text component to be sent.
 */
public fun CommandContext.fail(component: Component): Nothing {
    val out = Component.text().color(NamedTextColor.RED)
        .append(component)
    source.sendMessage(out)
    throw CommandCancelFlowException
}

/**
 * A DSL scope for registering commands.
 */
@JvmInline
@CommandDslMarker
public value class VelocityCommandRegisteringScope(
    private val extension: VelocityModule
) {
    /** DSL accessor. */
    @CommandDslMarker
    public operator fun <R> invoke(block: VelocityCommandRegisteringScope.() -> R): R =
        block()

    /**
     * Plus operator prefix for registering a command.
     *
     * ## Examples
     *
     * ```kotlin
     * extension.commands { +HelpCommand() }
     * ```
     */
    public operator fun BrigadierCommandDsl<CommandSource>.unaryPlus(): Unit =
        install(this)

    /**
     * Registers a command.
     *
     * ## Examples
     *
     * ```kotlin
     * extension.commands { install(HelpCommand()) }
     */
    public fun install(meta: CommandMeta, cmd: Command): Unit =
        extension.proxyServer.commandManager.register(meta, cmd)

    /** Registers a command.
     *
     * @see install
     *
     * ## Examples
     *
     * ```kotlin
     * extension.commands { install(HelpCommand()) }
     * ```
     */
    public fun install(vararg command: BrigadierCommandDsl<CommandSource>): Unit =
        command.forEach {
            val cmd = BrigadierCommand(it.tree.build())
            val meta = extension.proxyServer.commandManager.metaBuilder(cmd)
                .aliases(*it.definition.names.drop(1).toTypedArray())
            install(meta.build(), cmd)
        }
}

/** A DSL scope for registering commands. */
public inline val VelocityModule.commands: VelocityCommandRegisteringScope
    get() = VelocityCommandRegisteringScope(this)

public typealias BrigadierDsl =
        BrigadierCommandDsl<CommandSource>.() -> Unit

/**
 * A Kotlin DSL for Mojang's Brigadier library.
 * @param names Name and aliases of this command.
 */
@CommandDslMarker
public fun VelocityModule.command(
    names: List<String>,
    permission: String? = null,
    treeApply: LiteralBuilderDsl<CommandSource> = {},
    block: BrigadierDsl
): BrigadierCommandDsl<CommandSource> {
    require(names.all(String::isNotEmpty)) {
        "Command name must be not empty."
    }
    return BrigadierCommandDsl(
        BrigadierCommandDefinition(
            names = names,
            permission = permission
        ),
        this,
        VelocityCommandFrameworkPlatform(this),
        treeApply
    ).apply(block)
}

/**
 * A Kotlin DSL for Mojang's Brigadier library.
 * @param name Name of this command.
 */
@CommandDslMarker
public fun VelocityModule.command(
    name: String,
    permission: String? = null,
    treeApply: LiteralBuilderDsl<CommandSource> = {},
    block: BrigadierDsl
): BrigadierCommandDsl<CommandSource> {
    require(name.isNotEmpty()) { "Command name must be not empty." }
    return command(listOf(name), permission, treeApply, block)
}

