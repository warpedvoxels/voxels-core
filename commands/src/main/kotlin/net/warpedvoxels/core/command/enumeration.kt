@file:JvmName("CommandEnumSubcommand")

package net.warpedvoxels.core.command

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public fun String.snakecase(): String = split("([a-z])([A-Z]+)".toRegex())
    .joinToString("_")
    .lowercase()

public fun <E : Enum<E>> Enum<E>.lowercase(): String = name.lowercase()

public fun <E : Enum<E>> Enum<E>.uppercase(): String = name.uppercase()

public fun <E : Enum<E>> Enum<E>.snakecase(): String = name.snakecase()

@CommandDslMarker
public inline fun <reified E : Enum<E>, S> UsesCommandTree<S>.literals(
    name: (E) -> String = Enum<E>::lowercase,
    permission: (E) -> String? = { null },
    keep: Boolean = true,
    crossinline block: (value: E, transformedName: String) -> Unit = { _, _ -> }
): List<BrigadierLiteralCommandNode<S>> =
    enumValues<E>().map { value ->
        val transformedName = name(value)
        tree.literal(transformedName, permission(value), keep) {
            block(value, transformedName)
        }
    }


/**
 * Defines command execution logic that manipulates the command feedback.
 */
@OptIn(ExperimentalContracts::class)
@CommandDslMarker
public infix fun <S> Collection<UsesCommandTree<S>>.runs(command: CommandExec<S>) {
    contract {
        callsInPlace(command, InvocationKind.UNKNOWN)
    }
    forEach { it.tree runs command }
}

/**
 * Defines command execution logic that doesn't manipulate the command
 * feedback.
 */
@OptIn(ExperimentalContracts::class)
@CommandDslMarker
public infix fun <S> Collection<UsesCommandTree<S>>.executes(command: CommandUExec<S>) {
    contract {
        callsInPlace(command, InvocationKind.AT_LEAST_ONCE)
    }
    forEach { it.tree executes command }
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
public fun <S> Collection<UsesCommandTree<S>>.literal(
    name: String,
    permission: String? = null,
    keep: Boolean = true,
    treeApply: LiteralBuilderDsl<S> = {},
    block: TreeBuilderDsl<S> = {},
): List<BrigadierLiteralCommandNode<S>> {
    contract {
        callsInPlace(treeApply, InvocationKind.EXACTLY_ONCE)
    }
    return map { it.tree.literal(name, permission, keep, treeApply, block) }
}