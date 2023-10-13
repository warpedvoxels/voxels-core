package net.warpedvoxels.core.compose.hud.layout

import net.kyori.adventure.text.format.TextColor
import net.warpedvoxels.core.compose.hud.renderer.Surface
import net.warpedvoxels.core.compose.hud.renderer.SurfaceData
import net.warpedvoxels.core.compose.hud.shader.HudAnchoringShader.color

public sealed class NodeAnchor(
    public open val surface: Surface<out SurfaceData>,
    public open val color: TextColor? = null,
) {
    public data object AboveBars : NodeAnchor(Surface.ActionBar)

    public data object TopLeft : NodeAnchor(Surface.BossBar, color(0x0, 0xC))

    public data object TopCenter : NodeAnchor(Surface.BossBar)

    public data object TopRight : NodeAnchor(Surface.BossBar, color(0x0, 0x1))
}