@file:JvmName("HudRenderer")

package net.warpedvoxels.core.compose.hud.renderer

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.format.TextColor
import net.warpedvoxels.core.compose.hud.component.TextRelativeAlignment
import net.warpedvoxels.core.compose.hud.layout.CanvasRootNode
import net.warpedvoxels.core.compose.hud.layout.ComponentNode
import net.warpedvoxels.core.compose.hud.layout.RootComponentNode

private val nodeSelector: (ComponentNode) -> TextRelativeAlignment? = { it.options.relativeAlignment }

public interface Renderer {
    public fun render(audience: Audience)

    public fun dispose(audience: Audience)

    public companion object {
        public operator fun invoke(render: (Audience) -> Unit, dispose: (Audience) -> Unit): Renderer =
            object: Renderer {
                override fun render(audience: Audience): Unit = render(audience)

                override fun dispose(audience: Audience): Unit = dispose(audience)
            }
    }
}

public val EmptyRenderer: Renderer = Renderer({}, {})

private fun ComponentNode.renderRecursively(): Component {
    return children.sortedBy(nodeSelector).fold(this.component) { acc, node ->
        Component.join(JoinConfiguration.noSeparators(), acc, node.renderRecursively())
    }
}

internal fun CanvasRootNode.render() {
    if (children.size <= 1) {
        val element = children.firstOrNull() ?: return
        return canvas.set(element.options.anchor.surface, 0, element.renderRecursively())
    }
    val children = node.children.groupBy { it.options.anchor }
    for ((anchor, child) in children) {
        val components = child.map {
            it.renderRecursively().let { c ->
                if (anchor.color != null) c.color(TextColor.fromHexString(anchor.color!!)) else c
            }
        }
        components.forEachIndexed { index, component ->

        }
    }
    val node = RootComponentNode(this.node)
}