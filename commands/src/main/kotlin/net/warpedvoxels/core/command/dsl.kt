@file:JvmName("BrigadierCommandDSL")

package net.warpedvoxels.core.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.mojang.brigadier.tree.LiteralCommandNode
import kotlinx.coroutines.CoroutineScope
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import com.mojang.brigadier.Command as BrigadierCommand

@DslMarker
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
public annotation class CommandDslMarker

public typealias CommandExec<S> = BrigadierCommandExecutionContext<S>.() -> Int

public typealias CommandUExec<S> = BrigadierCommandExecutionContext<S>.() -> Unit

public typealias LiteralBuilderDsl<S> = LiteralArgumentBuilder<S>.() -> Unit

public typealias SyntaxErrorHandler<S> = BrigadierCommandExecutionContext<S>.
    (Exception) -> Unit

/**
 * The context for the ongoing process of a command execution.
 * @param ctx The original context provided by Brigadier.
 * @param S   The type for command sources.
 */
@JvmInline
public value class BrigadierCommandExecutionContext<S>(
    public val ctx: CommandContext<S>
) {
    public inline val source: S
        get() = ctx.source
}

/**
 * A DSL command framework built on top of [Mojang's Brigadier] library.
 * It is designed to be extensible and easy to use while not keeping you away from
 * the original Brigadier API.
 *
 * This is a simple wrapper around a Brigadier command tree that manages the root
 * node and translates DSL syntax to Brigadier's declarative code.
 *
 * @param definition The command definition information.
 * @param coroutineScope The coroutine scope to be used for command execution.
 * @param platform The platform to be used for command execution.
 * @param treeApply The DSL callback for the root command.
 * @param S The type for command sources.
 */
public class BrigadierCommandDsl<S>(
    public val definition: BrigadierCommandDefinition,
    coroutineScope: CoroutineScope,
    platform: CommandFrameworkPlatform<S>,
    treeApply: LiteralBuilderDsl<S> = {},
) : UsesCommandTree<S> {
    override val tree: BrigadierLiteralCommandNode<S> = BrigadierLiteralCommandNode(
        definition.names.first().lowercase(Locale.ENGLISH),
        definition.permission,
        coroutineScope,
        platform,
        null,
        treeApply
    )

    /**
     * Defines command execution logic that manipulates the command feedback.
     */
    @OptIn(ExperimentalContracts::class)
    @CommandDslMarker
    public infix fun runs(command: CommandExec<S>): BrigadierCommandDsl<S> {
        contract {
            callsInPlace(command, InvocationKind.UNKNOWN)
        }
        tree.runs(command)
        return this
    }

    /**
     * Defines command execution logic that doesn't manipulate the command
     * feedback.
     */
    @OptIn(ExperimentalContracts::class)
    @CommandDslMarker
    public infix fun executes(command: CommandUExec<S>): BrigadierCommandDsl<S> {
        contract {
            callsInPlace(command, InvocationKind.UNKNOWN)
        }
        tree.executes(command)
        return this
    }

    /**
     * Creates a child command literal node that might act within the context
     * of the parent command and registers it to this command tree.
     *
     * @param name The name of the subcommand.
     * @param block The DSL callback for the subcommand.
     * @param keep Whether arguments from the parent command should be
     *             kept when executing the subcommand. This also decides
     *             if this literal should be placed at the root or as last
     *              child of the parent command.
     */
    @OptIn(ExperimentalContracts::class)
    @CommandDslMarker
    public fun literal(
        name: String,
        permission: String? = definition.permission,
        keep: Boolean = true,
        treeApply: LiteralBuilderDsl<S> = {},
        block: TreeBuilderDsl<S> = {},
    ): BrigadierLiteralCommandNode<S> {
        contract {
            callsInPlace(treeApply, InvocationKind.EXACTLY_ONCE)
        }
        return tree.literal(name, permission, keep, treeApply, block)
    }

    /**
     * Compiles the DSL syntax tree to a list of Brigadier literal command nodes
     * given the labels of the definition of this command.
     */
    public fun build(): List<LiteralCommandNode<S>> {
        val head = tree.build().build()
        val result = mutableListOf(head)
        definition.names.drop(1).forEach {
            val alias = LiteralArgumentBuilder
                .literal<S>(it.lowercase(Locale.ENGLISH))
                .requires(head.requirement)
                .forward(
                    head.redirect,
                    head.redirectModifier,
                    head.isFork
                )
                .executes(head.command)
            head.children.forEach { child ->
                alias.then(child)
            }
            result.add(alias.build())
        }
        return result
    }

    /**
     * Provides argument delegation support and auto-registering.
     */
    public operator fun <T, B> CommandArgument<S, T, B>.provideDelegate(
        ref: Any?,
        prop: KProperty<*>
    ): ReadOnlyProperty<Any?, T> =
        with(tree) {
            provideDelegate(ref, prop)
        }
}

public typealias TreeBuilderDsl<S> = BrigadierLiteralCommandNode<S>.
    () -> Unit

/**
 * Builds a tree that follows the subcommand and argument order of
 * a command defined on Mojang's Brigadier command library.
 */
public class BrigadierLiteralCommandNode<S>(
    private val name: String,
    private val permission: String?,
    public val coroutineScope: CoroutineScope,
    public val platform: CommandFrameworkPlatform<S>,
    private val parent: AtomicReference<BrigadierLiteralCommandNode<S>>? = null,
    private val apply: LiteralBuilderDsl<S> = {}
) : UsesCommandTree<S> {
    private var context: BrigadierCommandExecutionContext<S>? = null
        set(value) {
            field = value
            parent?.get()?.context = value
        }

    private var execute: CommandExec<S>? = null

    private val arguments: MutableSet<CommandArgument<S, Any?, Any?>> =
        linkedSetOf()

    private val subcommands: MutableList<BrigadierLiteralCommandNode<S>> =
        mutableListOf()

    override val tree: BrigadierLiteralCommandNode<S>
        get() = this

    /**
     * Defines command execution logic that doesn't manipulate the command result.
     */
    @OptIn(ExperimentalContracts::class)
    @CommandDslMarker
    public infix fun runs(command: CommandExec<S>): BrigadierLiteralCommandNode<S> {
        contract {
            callsInPlace(command, InvocationKind.UNKNOWN)
        }
        this.execute = command
        return this
    }

    /**
     * Defines command execution logic that doesn't manipulate the command result
     * thus not requiring an integer value to be explicitly returned.
     */
    @OptIn(ExperimentalContracts::class)
    @CommandDslMarker
    public infix fun executes(command: CommandUExec<S>): BrigadierLiteralCommandNode<S> {
        contract {
            callsInPlace(command, InvocationKind.UNKNOWN)
        }
        runs {
            command()
            BrigadierCommand.SINGLE_SUCCESS
        }
        return this
    }

    /**
     * Registers a child argument into this tree.
     * @param child The argument to be registered.
     */
    @Suppress("UNCHECKED_CAST", "MemberVisibilityCanBePrivate")
    public fun <T : Any?, B : Any?> argument(
        child: CommandArgument<S, T, B>
    ): CommandArgument<S, T, B> = child.also {
        val last = arguments.lastOrNull()
        if (last != null && last is CommandArgument.Optional) {
            throw IllegalArgumentException("Cannot register required argument after an optional one.")
        }
        arguments.add(it as CommandArgument<S, Any?, Any?>)
    }

    /**
     * Creates a child command literal node that might act within the context
     * of the parent command and registers it to this command tree.
     *
     * @param name The name of the subcommand.
     * @param block The DSL callback for the subcommand.
     * @param keep Whether arguments from the parent command should be
     *             kept when executing the subcommand. This also decides
     *             if this literal should be placed at the root or as last
     *             child of the parent command.
     */
    @OptIn(ExperimentalContracts::class)
    @CommandDslMarker
    public fun literal(
        name: String,
        permission: String? = this.permission,
        keep: Boolean = true,
        block: LiteralBuilderDsl<S> = {},
        apply: TreeBuilderDsl<S> = {}
    ): BrigadierLiteralCommandNode<S> {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }
        return BrigadierLiteralCommandNode(
            name, permission, coroutineScope, platform,
            if (keep) AtomicReference(this) else null, block
        ).apply(apply).also(subcommands::add)
    }

    /**
     * Provides argument delegation support and auto-registering.
     */
    public operator fun <T, B> CommandArgument<S, T, B>.provideDelegate(
        ref: Any?,
        prop: KProperty<*>
    ): ReadOnlyProperty<Any?, T> {
        argument(this)
        return ReadOnlyProperty { _, _ ->
            if (context == null) {
                throw IllegalAccessException("Cannot access argument outside of commands.")
            }
            get(context!!.ctx, prop)
        }
    }

    /** Builds this tree into a [LiteralArgumentBuilder]. */
    public fun build(): LiteralArgumentBuilder<S> {
        val node = LiteralArgumentBuilder.literal<S>(name).apply(apply)
        if (permission != null) {
            node.requires {
                platform.hasPermission(it, permission)
            }
        }
        val run = Command { ctx ->
            context = BrigadierCommandExecutionContext(ctx)
            if (execute != null) {
                try {
                    execute!!(context!!)
                } catch (_: CommandCancelFlowException) {
                    -1
                }
            } else Command.SINGLE_SUCCESS
        }
        val arguments = if (parent != null) parent.get().arguments + arguments else this.arguments
        val hasArguments = arguments.isNotEmpty()
        subcommands.forEach {
            if (it.parent != null) node.then(it.build().build())
        }
        if (hasArguments) {
            val beginsOptional = arguments.indexOfFirst { t -> t is CommandArgument.Optional }
            val nodes = arguments.map(CommandArgument<S, *, *>::brigadier)
            val executionNodesStartIndex = if (beginsOptional == -1) nodes.lastIndex else beginsOptional - 1
            for (index in executionNodesStartIndex..<nodes.size) {
                (nodes.getOrNull(index) ?: node).executes(run)
            }
            nodes.reduceRight { `this`, lookbehind ->
                lookbehind.then(`this`)
                `this`
            }
            node.then(nodes.first().build())
        } else {
            node.executes(run)
        }
        subcommands.forEach {
            if (it.parent != null) node.then(it.build().build())
        }
        return node
    }
}

public typealias SuggestionsDsl<S> = suspend SuggestionsBuilderDsl<S>.() -> Unit

/**
 * Simple wrapper class around command suggestion building.
 */
public data class SuggestionsBuilderDsl<S>(
    val ctx: CommandContext<S>,
    val builder: SuggestionsBuilder
) {
    /**
     * Suggests a collection of values.
     */
    public fun suggest(vararg values: Any): Unit = values.forEach {
        when (it) {
            is Int -> builder.suggest(it)
            is SuggestionsBuilder -> builder.add(it)
            else -> builder.suggest(it.toString())
        }
    }
}

public interface UsesCommandTree<S> {
    public val tree: BrigadierLiteralCommandNode<S>
}
