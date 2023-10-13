@file:JvmName("HudCoreShaders")

package net.warpedvoxels.core.compose.hud.shader

import net.warpedvoxels.core.rp.compilation.NamespacedFileProvider
import org.bukkit.NamespacedKey

public enum class CoreShaderType(public val key: NamespacedKey) {
    RenderTypeGui("rendertype_gui"),
    RenderTypeText("rendertype_text"),
    RenderTypeTextBackground("rendertype_text_background");

    constructor(name: String): this(NamespacedKey.minecraft(name))
}

public interface CoreShader {
    public val type: CoreShaderType
    
    public val providers: List<NamespacedFileProvider>

    public fun combine(other: CoreShader): CoreShader?
}

public operator fun CoreShader.plus(other: CoreShader): CoreShader = combine(other) ?: other.combine(this)
    ?: throw IllegalArgumentException("Unsupported core shader combination.")