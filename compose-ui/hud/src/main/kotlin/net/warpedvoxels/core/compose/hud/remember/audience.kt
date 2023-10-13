package net.warpedvoxels.core.compose.hud.remember

import androidx.compose.runtime.*
import net.kyori.adventure.audience.Audience

@Composable
public fun rememberAudience(): State<MutableState<Audience>> = remember {
    derivedStateOf { mutableStateOf(Audience.empty()) }
}