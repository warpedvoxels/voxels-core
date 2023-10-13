package net.warpedvoxels.core.compose.hud.component

import net.kyori.adventure.text.Component
import net.warpedvoxels.core.compose.hud.layout.NodeAnchor

public enum class TextRelativeAlignment {
    Left, Middle, Right
}

public open class StyleOptions(
    public var anchor: NodeAnchor,
    public var relativeAlignment: TextRelativeAlignment = TextRelativeAlignment.Middle,
    public var componentModifier: Component.() -> Unit = {}
) {

    public fun anchor(anchor: NodeAnchor): StyleOptions = StyleOptions(anchor, relativeAlignment)

    public fun topLeft(): StyleOptions = anchor(NodeAnchor.TopLeft)

    public fun topCenter(): StyleOptions = anchor(NodeAnchor.TopCenter)

    public fun topRight(): StyleOptions = anchor(NodeAnchor.TopRight)

    public fun aboveBars(): StyleOptions = anchor(NodeAnchor.AboveBars)

    public fun alignment(alignment: TextRelativeAlignment): StyleOptions = StyleOptions(anchor, alignment)

    public fun atLeft(): StyleOptions = alignment(TextRelativeAlignment.Left)

    public fun atMiddle(): StyleOptions = alignment(TextRelativeAlignment.Middle)

    public fun atRight(): StyleOptions = alignment(TextRelativeAlignment.Right)

    public fun componentSettings(settings: Component.() -> Unit) {
        val current = componentModifier
        componentModifier = {
            current()
            settings()
        }
    }


    public companion object : StyleOptions(NodeAnchor.AboveBars, TextRelativeAlignment.Middle)
}