package net.warpedvoxels.core.compose.hud.component

import androidx.compose.runtime.*
import net.kyori.adventure.text.Component
import net.warpedvoxels.core.compose.hud.layout.Node
import net.warpedvoxels.core.compose.hud.remember.rememberAudience
import net.warpedvoxels.core.compose.hud.renderer.Surface
import net.warpedvoxels.core.compose.hud.renderer.SurfaceData
import net.warpedvoxels.core.compose.hud.renderer.SurfacePair

public val LocalSurfacePair: ProvidableCompositionLocal<SurfacePair<out SurfaceData>> =
    compositionLocalOf { error("No local surface defined.") }

@Composable
internal fun BossBar(value: Component, options: StyleOptions) {
    val audience by rememberAudience()
    val bossBar: Surface.BossBar.Data = remember(value, options) {
        Surface.BossBar.create(value).also {

        }
    }
}

@Composable
public fun Text(value: Component, options: StyleOptions = StyleOptions) {
    CompositionLocalProvider(LocalSurfacePair provides ) {
        Node(renderer) {}
    }
}

@Composable
public fun Text(value: String, options: StyleOptions = StyleOptions) {
    Text(Component.text(value), options)
}
