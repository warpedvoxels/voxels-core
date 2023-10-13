package net.warpedvoxels.core.compose.hud

import androidx.compose.runtime.Composable
import net.warpedvoxels.core.compose.hud.component.GameViewport
import net.warpedvoxels.core.compose.hud.component.Text
import net.warpedvoxels.core.compose.hud.component.StyleOptions
import org.bukkit.entity.Player

@Composable
public fun Player.DefaultHud() {
    GameViewport(this) {
        Top {
            Text()
        }
        ActionBar {
            Row()
            Row()
        }
        Text("Hello, world!", options = StyleOptions.aboveBars())
    }
}

public suspend fun A(): Unit = hud {

}