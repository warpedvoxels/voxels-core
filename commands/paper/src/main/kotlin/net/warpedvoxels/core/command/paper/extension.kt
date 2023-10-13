@file:JvmName("PaperCommandExtension")

package net.warpedvoxels.core.command.paper

import io.papermc.paper.adventure.PaperAdventure
import net.kyori.adventure.text.Component
import net.minecraft.commands.CommandSourceStack
import net.warpedvoxels.core.command.*
import net.warpedvoxels.core.architecture.VoxelsPlugin
import net.warpedvoxels.core.craftbukkit.CraftServer
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

public typealias CommandContext =
        BrigadierCommandExecutionContext<CommandSourceStack>

context(CommandContext)
public fun CommandSourceStack.player(): Player =
    bukkitSender as? Player ?: fail("Unexpected sender type.")

public inline val CommandSourceStack.bukkitPlayer: Player?
    get() = bukkitSender as? Player

public inline val CommandSourceStack.bukkit: CommandSender
    get() = bukkitSender

/**
 * Respond a command with successful feedback.
 * @param text The text component to be sent.
 */
public fun CommandContext.respond(text: String): Int = Success.also {
    source.sendSuccess({
        net.minecraft.network.chat.Component.literal(text)
    }, false)
}

/**
 * Respond a command with successful feedback.
 * @param text The text component to be sent.
 */
public fun CommandContext.respond(text: Component): Int = Success.also {
    source.sendSuccess({ PaperAdventure.asVanilla(text) }, false)
}

/**
 * Respond a command with unsuccessful feedback.
 * @param text The text component to be sent.
 */
public fun CommandContext.fail(text: String): Nothing {
    source.sendFailure(net.minecraft.network.chat.Component.literal(text), true)
    throw CommandCancelFlowException
}

/**
 * Respond a command with unsuccessful feedback.
 * @param component The text component to be sent.
 */
public fun CommandContext.fail(component: Component): Nothing {
    source.sendFailure(PaperAdventure.asVanilla(component), true)
    throw CommandCancelFlowException
}

/**
 * Respond a command with unsuccessful feedback.
 * @param component The text component to be sent.
 */
public fun CommandContext.fail(
    component: net.minecraft.network.chat.Component
): Nothing {
    source.sendFailure(component, true)
    throw CommandCancelFlowException
}

/**
 * A DSL scope for registering commands.
 */
@JvmInline
@CommandDslMarker
public value class BukkitCommandRegisteringScope(
    private val plugin: VoxelsPlugin
) {
    /** DSL accessor. */
    @CommandDslMarker
    public operator fun <R> invoke(block: BukkitCommandRegisteringScope.() -> R): R =
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
    public operator fun Command.unaryPlus(): Boolean =
        install(this)

    /**
     * Plus operator prefix for registering a command.
     *
     * ## Examples
     *
     * ```kotlin
     * extension.commands { +HelpCommand() }
     * ```
     */
    public operator fun BrigadierCommandDsl<CommandSourceStack>.unaryPlus(): Boolean =
        install(BukkitBrigadierCommandWrapper(this))

    /**
     * Registers a command.
     *
     * ## Examples
     *
     * ```kotlin
     * extension.commands { install(HelpCommand()) }
     */
    public fun install(vararg command: Command): Boolean =
        command.map {
            (plugin.server as CraftServer).commandMap.register(
                plugin.namespace, it
            )
        }.all { it }

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
    public fun install(vararg command: BrigadierCommandDsl<CommandSourceStack>): Boolean =
        install(*command.map(::BukkitBrigadierCommandWrapper).toTypedArray())
}

/** A DSL scope for registering commands. */
public inline val VoxelsPlugin.commands: BukkitCommandRegisteringScope
    get() = BukkitCommandRegisteringScope(this)

public typealias BrigadierDsl =
        BrigadierCommandDsl<CommandSourceStack>.() -> Unit

/**
 * A Kotlin DSL for Mojang's Brigadier library.
 * @param names Name and aliases of this command.
 */
@CommandDslMarker
public fun VoxelsPlugin.command(
    names: List<String>,
    permission: String? = null,
    treeApply: LiteralBuilderDsl<CommandSourceStack> = {},
    block: BrigadierDsl = {}
): BrigadierCommandDsl<CommandSourceStack> {
    require(names.all(String::isNotEmpty)) {
        "Command name must be not empty."
    }
    return BrigadierCommandDsl(
        BrigadierCommandDefinition(
            names = names,
            permission = permission
        ),
        this,
        PaperCommandFrameworkPlatform,
        treeApply = treeApply
    ).apply(block)
}

/**
 * A Kotlin DSL for Mojang's Brigadier library.
 * @param name Name of this command.
 */
@CommandDslMarker
public fun VoxelsPlugin.command(
    name: String,
    permission: String? = null,
    treeApply: LiteralBuilderDsl<CommandSourceStack> = {},
    block: BrigadierDsl = {}
): BrigadierCommandDsl<CommandSourceStack> {
    require(name.isNotEmpty()) { "Command name must be not empty." }
    return command(listOf(name), permission, treeApply, block)
}

