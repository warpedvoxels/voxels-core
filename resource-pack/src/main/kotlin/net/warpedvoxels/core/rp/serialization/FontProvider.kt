package net.warpedvoxels.core.rp.serialization

import kotlinx.serialization.Serializable

public typealias BitmapFont = FontProvider.Bitmap

@Serializable
public sealed class FontProvider(public val type: String) {
    /**
     * A bitmap font.
     *
     * @param file   The resource location of the used file, starting from `assets/minecraft/textures` by default.
     *               Prefacing the location with `<namespace>`: changes the location to `assets/<namespace>/textures`.
     * @param height The height of the character, measured in pixels. It can be negative. This tag is separate
     *               from the area used in the source texture and just rescales the displayed result.
     * @param ascent The ascent of the character, measured in pixels. This value adds a vertical shift to the displayed
     *               result.
     * @param chars  A list of strings containing the characters replaced by this provider, as well as their order
     *               within the texture. All elements must describe the same number of characters. The texture is split
     *               into one equally sized row for each element of this list. Each row is split into one equally sized
     *               character for each character within a list element.
     */
    @Serializable
    public data class Bitmap(
        public var height: Int = 8,
        public var ascent: Int = height,
        public var chars: List<String> = emptyList(),
        public var file: String = ""
    ): FontProvider("bitmap")

    /**
     * A TrueType font or OpenType font. Despite its name, it supports both TTF and OTF.
     *
     * @param file The resource location of the TrueType/OpenType font file within `assets/<namespace>/font`.
     * @param shift The distance by which the characters of this provider are shifted.
     * @param size Font size to render at.
     * @param oversample Resolution to render at, increasing anti-aliasing factor.
     * @param skip String of characters or array of characters to exclude.
     */
    @Serializable
    public data class TTF(
        var file: String,
        var shift: List<Float>,
        var size: Float,
        var oversample: Float,
        var skip: String,
    ): FontProvider("ttf")

    /**
     * Show chosen characters as spaces.
     * @param advances The number of pixels that the following characters are moved to the right. It can be negative.
     *                 Decimal numbers can be used for precise movement on higher gui scales.
     */
    @Serializable
    public data class Space(var advances: Map<String, Float>): FontProvider("space")
}

public data class FontProviders(var providers: Set<FontProvider>)