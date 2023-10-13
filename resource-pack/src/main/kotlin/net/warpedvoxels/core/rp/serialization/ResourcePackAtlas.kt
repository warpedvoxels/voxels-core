package net.warpedvoxels.core.rp.serialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Atlases are configuration files, located in `atlases` directory, that control which images are included in the
 * atlases.
 *
 * @param sources Contains a list of sources. Every entry runs in during load, in order of definition, adding or
 *                removing new files to the texture list.
 */
@Serializable
public data class ResourcePackAtlas(var sources: List<ResourcePackAtlasSource>)

/**
 * Atlases are configuration files, located in `atlases` directory, that control which images are included in the
 * atlases.
 */
@Serializable
public sealed class ResourcePackAtlasSource(public val type: String) {
    /**
     * Lists all files in a directory and its subdirectories, across all namespaces.
     *
     * @param source Directory in pack to be listed (relative to `textures` directory).
     * @param prefix String to be prepended to the sprite name when loaded.
     */
    @Serializable
    public data class Directory(var source: String, var prefix: String): ResourcePackAtlasSource("directory")

    /**
     * Adds a single file.
     *
     * @param resource Location of a resource within the pack (relative to `textures` directory, implied `.png`
     *                 extension).
     * @param sprite   Sprite name (optional, defaults to `resource`).
     */
    @Serializable
    public data class Single(var resource: String, var sprite: String): ResourcePackAtlasSource("single")

    /**
     * Removes sprites matching the given pattern (only works for entries already in the list).
     *
     * @param namespace Regular expression of the namespace identification to be removed, if omitted, any value will be
     *                  matched.
     * @param path      Regular expression of the path to be removed, if omitted, any value will be matched.
     */
    @Serializable
    public data class Filter(var namespace: String, var path: String): ResourcePackAtlasSource("filter")

    /**
     * Copies rectangular regions from other images.
     *
     * @param resource Location of a resource within the pack (relative to `textures` directory, implied `.png`
     *                 extension).
     * @param divisorX Used for determining the units used by in the `x` coordinate of regions.
     * @param divisorY Used for determining the units used by in the `y` coordinate of regions.
     * @param regions  List of regions to copy from the source image.
     */
    @Serializable
    public data class Unstitch(
        var resource: String,
        @SerialName("divisor_x")
        var divisorX: Double,
        @SerialName("divisor_y")
        var divisorY: Double,
        var regions: Regions,
    ): ResourcePackAtlasSource("unstitch") {
        /**
         * List of regions to copy from the source image.
         *
         * @param sprite Specifies the sprite name.
         * @param x Specifies the `x` coordinate of the top-left corner of the region.
         * @param y Specifies the `y` coordinate of the top-left corner of the region.
         * @param width Specifies the width of the region.
         * @param height Specifies the width of the region.
         */
        @Serializable
        public data class Regions(
            var sprite: String,
            var x: Double,
            var y: Double,
            var width: Double,
            var height: Double
        )
    }
}