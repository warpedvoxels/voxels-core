package net.warpedvoxels.core.compose.hud.renderer

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.bossbar.BossBar.*
import net.kyori.adventure.text.Component

public sealed interface SurfaceData {
    public var component: Component
}

public data class SurfacePair<D: SurfaceData>(val surface: Surface<D>, var data: D)

public sealed interface Surface<D: SurfaceData> {
    public fun render(audience: Audience, data: D)

    public fun dispose(audience: Audience, data: D)

    public fun create(component: Component): D

    public data object BossBar : Surface<BossBar.Data> {
        @JvmInline
        public value class Data(public val bossBar: net.kyori.adventure.bossbar.BossBar): SurfaceData {
            override var component: Component
                get() = bossBar.name()
                set(value) {
                    bossBar.name(value)
                }
        }

        override fun render(audience: Audience, data: Data): Unit =
            audience.showBossBar(data.bossBar)

        override fun dispose(audience: Audience, data: Data): Unit =
            audience.hideBossBar(data.bossBar)

        override fun create(component: Component): Data =
            Data(bossBar(component, 1f, Color.WHITE, Overlay.NOTCHED_20))
    }

    public data object ActionBar : Surface<ActionBar.Data> {
        public data class Data(override var component: Component): SurfaceData

        override fun render(audience: Audience, data: Data): Unit =
            audience.sendActionBar(data.component)

        override fun dispose(audience: Audience, data: Data): Unit =
            audience.sendActionBar(Component.empty())

        override fun create(component: Component): Data =
            Data(component)
    }
}
