package net.warpedvoxels.core.command.paper

import com.destroystokyo.paper.event.brigadier.CommandRegisteredEvent
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.adventure.PaperAdventure
import io.papermc.paper.brigadier.PaperBrigadier
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandRuntimeException
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.CommonComponents
import net.warpedvoxels.core.command.BrigadierCommandDsl
import net.warpedvoxels.core.architecture.VoxelsPlugin
import net.warpedvoxels.core.craftbukkit.VanillaCommandWrapper
import org.bukkit.command.CommandSender
import org.bukkit.command.PluginIdentifiableCommand
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.command.UnknownCommandEvent
import org.bukkit.plugin.Plugin
import kotlin.math.min

public fun CommandSender.source(): CommandSourceStack =
    VanillaCommandWrapper.getListener(this)

/**
 * A module for registering command suggestions from this library
 * Brigadier Kotlin DSL.
 */
public class BukkitBrigadierCommandWrapper(
    private val command: BrigadierCommandDsl<CommandSourceStack>,
) : PluginIdentifiableCommand, BukkitCommand(
    command.definition.names.first(),
    "",
    "/${command.definition.names.first()}",
    command.definition.names.drop(1),
) {
    public val literals: List<LiteralCommandNode<CommandSourceStack>> =
        command.build()

    private val dispatcher = CommandDispatcher<CommandSourceStack>()

    init {
        literals.forEach { literal ->
            dispatcher.root.addChild(literal)
        }
        permission = command.definition.permission
    }

    override fun getPlugin(): Plugin =
        command.tree.coroutineScope as? VoxelsPlugin
            ?: error("command.tree.coroutineScope must be an instance of VoxelPlugin.")

    private fun sendErrorComponent(
        ex: CommandSyntaxException,
        cmd: String,
        sender: CommandSender,
        stack: CommandSourceStack
    ) {
        val component = Component.text()
        component.color(NamedTextColor.RED)
            .append(PaperBrigadier.componentFromMessage(ex.rawMessage))
        if (ex.input != null && ex.cursor >= 0) {
            val cursor = min(ex.input.length, ex.cursor)
            val error = net.minecraft.network.chat.Component.empty()
                .withStyle(ChatFormatting.GRAY)
                .withStyle {
                    it.withClickEvent(
                        ClickEvent(
                            ClickEvent.Action.SUGGEST_COMMAND,
                            "/$cmd"
                        )
                    )
                }
            if (cursor > 10) {
                error.append(CommonComponents.ELLIPSIS)
            }
            error.append(ex.input.substring(0.coerceAtLeast(cursor - 10), cursor))
            if (cursor < ex.input.length) {
                error.append(
                    net.minecraft.network.chat.Component.literal(ex.input.substring(cursor))
                        .withStyle(
                            ChatFormatting.RED,
                            ChatFormatting.UNDERLINE
                        )
                )
            }
            error.append(
                net.minecraft.network.chat.Component.translatable("command.context.here")
                    .withStyle(
                        ChatFormatting.RED,
                        ChatFormatting.ITALIC
                    )
            )
            component.append(Component.newline())
                .append(PaperAdventure.asAdventure(error))
            val event = UnknownCommandEvent(
                sender, cmd, component.build()
            )
            plugin.server.pluginManager.callEvent(event)
            if (event.message() != null) {
                stack.sendFailure(PaperAdventure.asVanilla(event.message()), false)
            }
        }
    }

    @Suppress("NAME_SHADOWING")
    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
        val stack = sender.source()
        val args = if (args.isNotEmpty())
            " " + args.joinToString(" ")
        else ""
        val cmd = "$name$args"
        try {
            dispatcher.execute(cmd, stack)
        } catch (ex: CommandRuntimeException) {
            stack.sendFailure(ex.component)
        } catch (ex: CommandSyntaxException) {
            sendErrorComponent(ex, cmd, sender, stack)
        }
        return true
    }
}

/**
 * A module for registering command suggestions from this library
 * Brigadier Kotlin DSL.
 */
public object BukkitBrigadierCommandSuggestionsListener : Listener {
    @Suppress("UnstableApiUsage")
    @EventHandler
    public fun suggest(
        event: CommandRegisteredEvent<CommandSourceStack>
    ) {
        event.literal = (event.command as? BukkitBrigadierCommandWrapper
            ?: return).literals.first()
        event.isRawCommand = true
    }
}

/**
 * A module for registering command suggestions from this library
 * Brigadier Kotlin DSL.
 *
 * Installation is done the same way as other event listeners.
 *
 * ## Example
 *
 * ```kotlin
 * val MyCoreCommand.myCommand get() = command("mycommand") {
 *
 * }
 *
 * class MyCoreExtension: VoxelPlugin("my-core") {
 *     override suspend fun enable() {
 *         listeners {
 *             install(CommandSuggestions)
 *         }
 *         commands {
 *             install()
 *         }
 *     }
 * }
 * ```
 */
public typealias CommandSuggestions = BukkitBrigadierCommandSuggestionsListener
