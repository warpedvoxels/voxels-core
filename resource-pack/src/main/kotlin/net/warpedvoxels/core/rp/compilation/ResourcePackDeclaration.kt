package net.warpedvoxels.core.rp.compilation

import org.bukkit.NamespacedKey
import net.warpedvoxels.core.rp.serialization.FontProvider
import net.warpedvoxels.core.rp.serialization.ParticleProvider
import net.warpedvoxels.core.rp.serialization.SoundProvider
import net.warpedvoxels.core.rp.serialization.meta.ResourcePackEntrypoint
import net.warpedvoxels.core.rp.serialization.model.BlockModel
import net.warpedvoxels.core.rp.serialization.model.BlockStateModel
import net.warpedvoxels.core.rp.serialization.model.ItemModel

public typealias Namespaced<T> = Map<NamespacedKey, T>
public typealias MutableNamespaced<T> = MutableMap<NamespacedKey, T>

/**
 * Compilable resource-pack DSL-based declarations for a fully functional resource-pack.
 */
public data class ResourcePackDeclaration(
    val meta: ResourcePackEntrypoint,
    val sounds: Namespaced<SoundProvider> = emptyMap(),
    val particles: Namespaced<ParticleProvider> = emptyMap(),
    val fonts: Set<FontProvider> = emptySet(),
    val models: Models = Models(),
    val blocks: Set<BlockDeclaration> = emptySet(),
    val items: Set<ItemDeclaration> = emptySet(),
    val externalFiles: Set<NamespacedFileProvider> = emptySet(),
    val language: Set<MinecraftCustomLanguage> = emptySet()
) {
    public data class Models(
        val blockState: Namespaced<BlockStateModel> = emptyMap(),
        val block: Namespaced<BlockModel> = emptyMap(),
        val item: Namespaced<ItemModel> = emptyMap()
    ) {
        public fun blockState(location: String): BlockStateModel? = blockState[NamespacedKey.fromString(location)!!]
        public fun block(location: String): BlockModel? = block[NamespacedKey.fromString(location)!!]
        public fun item(location: String): ItemModel? = item[NamespacedKey.fromString(location)!!]
    }
}