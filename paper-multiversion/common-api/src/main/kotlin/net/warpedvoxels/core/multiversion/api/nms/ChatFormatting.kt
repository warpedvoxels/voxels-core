package net.warpedvoxels.core.multiversion.api.nms

import net.kyori.adventure.text.format.TextFormat

public interface ChatFormatting {
    public interface Companion {
        public companion object {
            public val STRIP_FORMATTING_PATTERN: Regex = Regex("(?i)\u00a7[0-9A-FK-OR]")
        }

        public fun getByHexValue(hex: Int): ChatFormatting?

        public fun stripFormatting(text: String?): String?

        public fun getByName(name: String): ChatFormatting?

        public fun getById(id: Int): ChatFormatting?

        public fun getByCode(code: Char): ChatFormatting?

        public fun getNames(colors: Boolean, modifiers: Boolean): MutableCollection<String>
    }

    /** The name of this chat formatter. */
    public val name: String

    /** The character used to prefix all chat formatting codes. */
    public val code: Char

    /** `true` if this chat formatter is not a color. */
    public val isFormat: Boolean

    /** The ordinal color index of this color. */
    public val id: Int

    /** Decimal representation of this color. */
    public val color: Int?
}