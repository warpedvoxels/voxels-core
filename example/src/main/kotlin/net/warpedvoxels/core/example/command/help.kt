@file:JvmName("HelpCommand")

package net.warpedvoxels.core.example.command

import net.warpedvoxels.core.command.argument.default
import net.warpedvoxels.core.command.argument.integer
import net.warpedvoxels.core.command.literals
import net.warpedvoxels.core.command.lowercase
import net.warpedvoxels.core.command.paper.command
import net.warpedvoxels.core.command.paper.respond
import net.warpedvoxels.core.example.ExampleCorePlugin

private enum class HelpSections {
    Discord, GitHub, Website;

    private fun repeatingList(count: Int, vararg elements: String): List<String> =
        buildList(count * elements.size) {
            repeat(count) { addAll(elements) }
        }

    fun elements(): List<String> = when (this) {
        Discord -> repeatingList(16, "Discord Page 1", "Discord Page 2", "Discord Page 3")
        GitHub -> repeatingList(8, "GitHub Page 1", "GitHub Page 2", "GitHub Page 3")
        Website -> repeatingList(13, "Website Page 1", "Website Page 2", "Website Page 3")
    }
}

internal val ExampleCorePlugin.HelpCommand
    get() = command(listOf("help", "h"), "voxels.help") {
        val page by integer("page").default { 1 }
        literals(HelpSections::lowercase) { section, _ ->
            runs {
                val chunks = section.elements().chunked(3)
                val chunk = chunks[page.coerceIn(1, chunks.size) - 1]
                respond("[$page/${chunks.size}] ${section.name}:\n${chunk.joinToString("\n")}")
            }
        }
        runs {
            respond("Help for:\n${HelpSections.entries.joinToString("\n") { it.name }}")
        }
    }