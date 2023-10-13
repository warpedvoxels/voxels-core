package net.warpedvoxels.core.rp.compilation

import net.kyori.adventure.key.Key
import net.warpedvoxels.core.rp.serialization.model.*
import org.bukkit.Material

public data class ItemDeclaration(
    val identifier: Key,
    val baseItem: Material,
    val index: UInt,
    val model: ItemModel,
) {
    init {
        require(baseItem.isItem && !baseItem.isEmpty && !baseItem.isInteractable) {
            "Base item must be an item, not a block or air."
        }
    }

    public companion object {
        public val DEFAULT_BASE_MATERIAL: Material = Material.PRISMARINE_SHARD
    }
}