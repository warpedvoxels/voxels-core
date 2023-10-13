package net.warpedvoxels.core.compose

import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope

public interface ComposableScope : CoroutineScope {
    public fun setContent(content: @Composable ComposableScope.() -> Unit)
}