

package net.warpedvoxels.proxy.core.command

import net.warpedvoxels.core.command.argument.integer
import net.warpedvoxels.core.command.velocity.command
import net.warpedvoxels.core.command.velocity.respond
import net.warpedvoxels.proxy.core.VelocityModule

fun VelocityModule.TestCommand() = command("test") {
    val times by integer("times")
    executes {
        repeat(times) { index ->
            respond("Hello! ${index + 1}")
        }
    }
    literal("single", keep = false) executes {
        respond("Hello! Single")
    }
}