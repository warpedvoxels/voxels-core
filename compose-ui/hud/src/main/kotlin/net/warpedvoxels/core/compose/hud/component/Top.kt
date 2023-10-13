@file:Suppress("UnusedReceiverParameter")

package net.warpedvoxels.core.compose.hud.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.warpedvoxels.core.compose.hud.layout.NodeAnchor
import net.warpedvoxels.core.compose.hud.renderer.Surface

public val LocalTopRowScope: ProvidableCompositionLocal<TopRowScope> =
    compositionLocalOf { error("No local top row scope defined.") }

public data object TopScope

@JvmInline
public value class TopRowScope(public val data: Surface.BossBar.Data) {
    public operator fun component1(): BossBar = data.bossBar
}

@Composable
public fun Top(content: @Composable TopScope.() -> Unit) {
    HudLayout {
        content(TopScope)
    }
}

@Composable
public fun TopScope.Row(content: @Composable TopRowScope.() -> Unit) {
    CompositionLocalProvider(LocalTopRowScope provides TopRowScope(Surface.BossBar.create(Component.empty()))) {
        HudLayout {
            content(LocalTopRowScope.current)
        }
    }
}

@Composable
public fun TopRowScope.Text(value: Component = Component.empty(), options: StyleOptions = StyleOptions) {
    require(options.anchor != NodeAnchor.AboveBars) { "Incompatible text anchor type." }
    val (bar) = LocalTopRowScope.current
    val color = options.anchor.color
    require(bar.name().children().size < 3) { "Exceeded row text limit (one per anchor type)." }
    require(bar.name().color() != color && bar.name().children().none { it.color() == color }) {
        "Exceeded row text limit (one per anchor type)."
    }

    val current: Component = bar.name().children().find { it.color() == options.anchor.color }
        ?: bar.name().append(value.color(options.anchor.color))
}