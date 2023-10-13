package net.warpedvoxels.core.compose.hud.component

import androidx.compose.runtime.*
import net.kyori.adventure.audience.Audience
import net.warpedvoxels.core.compose.hud.remember.rememberAudience

@Composable
public fun GameViewport(viewers: Audience, content: @Composable () -> Unit) {
    HudLayout {
        content()
    }
}