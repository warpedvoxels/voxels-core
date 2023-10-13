@file:JvmName("ComposeHudNode")

package net.warpedvoxels.core.compose.hud.layout

import androidx.compose.runtime.AbstractApplier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import net.kyori.adventure.audience.Audience
import net.warpedvoxels.core.compose.hud.component.LocalSurfacePair
import net.warpedvoxels.core.compose.hud.remember.rememberAudience
import net.warpedvoxels.core.compose.hud.renderer.EmptyRenderer
import net.warpedvoxels.core.compose.hud.renderer.Renderer
import java.awt.Canvas

public sealed interface Node : Renderer {
    public val children: MutableList<Node>

    public val isRoot: Boolean get() = false
}

@JvmInline
public value class RootNode(override val children: MutableList<Node> = mutableListOf()) : Node {
    override val isRoot: Boolean get() = true

    override fun render(audience: Audience): Unit = children.forEach {
        it.render(audience)
    }

    override fun dispose(audience: Audience): Unit = children.forEach {
        it.dispose(audience)
    }
}

public class MutableNode(
    public var renderer: Renderer = EmptyRenderer,
    override var children: MutableList<Node> = mutableListOf(),
) : Node {
    override fun render(audience: Audience): Unit = renderer.render(audience)

    override fun dispose(audience: Audience): Unit = renderer.dispose(audience)
}

//public sealed interface ComponentNode : Node {
//    public var component: Component
//    public var options: StyleOptions
//    public var height: Int
//}
//
//public data class TextNode(
//    override var component: Component = Component.empty(),
//    override var options: StyleOptions = StyleOptions,
//    override var children: MutableList<ComponentNode> = mutableListOf(),
//    var rendering: Audience.() -> Unit,
//) : ComponentNode {
//    override var height: Int = 7
//
//    override fun render(audience: Audience) {
//        rendering(audience)
//        children.forEach { it.render(audience) }
//    }
//}
//
//@JvmInline
//internal value class RootComponentNode(val root: RootNode): ComponentNode {
//    private fun getHighestHeight(node: ComponentNode, current: Int = node.height): Int {
//        var height: Int = current
//        for (child in node.children) {
//            val curr = getHighestHeight(child, height)
//            if (curr > height)
//                height = curr
//        }
//        return height
//    }
//    override var height: Int
//        get() = if (root.children.isNotEmpty()) getHighestHeight(root.children.first()) else 7
//        set(_) {
//            throw UnsupportedOperationException("Can't set height of root node.")
//        }
//
//    override val children: MutableList<ComponentNode>
//        get() = root.children
//
//    override fun render(audience: Audience) = root.render(audience)
//
//    override var component: Component
//        get() = Component.empty()
//        set(_) {
//            throw UnsupportedOperationException("Can't set component of root node.")
//        }
//
//    override var options: StyleOptions
//        get() = StyleOptions
//        set(_) {
//            throw UnsupportedOperationException("Can't set options of root node.")
//        }
//}

@Suppress("UNCHECKED_CAST")
public class NodeApplier(node: Node, private val onEndChanges: () -> Unit = {}) : AbstractApplier<Node>(node) {
    override fun onEndChanges(): Unit = onEndChanges.invoke()

    override fun insertTopDown(index: Int, instance: Node) {
        // Ignored, we insert bottom-up.
    }

    override fun insertBottomUp(index: Int, instance: Node) {
        instance.children.add(index, instance)
    }

    override fun remove(index: Int, count: Int) {
        current.children.remove(index, count)
    }

    override fun move(from: Int, to: Int, count: Int) {
        current.children.move(from, to, count)
    }

    override fun onClear() {
        current.children.clear()
    }
}

@Composable
public inline fun Node(renderer: Renderer, content: @Composable () -> Unit) {
    ComposeNode<MutableNode, NodeApplier>(
        factory = { MutableNode() },
        update = {
            set(renderer) {
                this.renderer = it
            }
        },
        content = content
    )
}