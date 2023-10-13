package net.warpedvoxels.core.compose.hud.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import net.warpedvoxels.core.compose.hud.layout.NodeApplier
import net.warpedvoxels.core.compose.hud.layout.RootNode

@Composable
public fun HudLayout(content: @Composable () -> Unit = {}) {
    ComposeNode<RootNode, NodeApplier>(
        factory = { RootNode() },
        update = {},
        content = content
    )
}