package net.warpedvoxels.core.multiversion.v1_20_R1.nms

import net.warpedvoxels.core.multiversion.api.nms.ChatFormatting
import net.minecraft.ChatFormatting as MinecraftChatFormatting
import net.warpedvoxels.core.multiversion.api.nms.ChatFormatting as ChatFormattingApi

@JvmInline
public value class ChatFormatting(public val minecraft: MinecraftChatFormatting): ChatFormattingApi {
    override val code: Char get() = minecraft.code

    override val isFormat: Boolean get() = minecraft.isFormat

    override val id: Int get() = minecraft.id

    override val color: Int? get() = minecraft.color

    override val name: String get() = minecraft.name

    override fun toString(): String = minecraft.toString()

    public companion object: ChatFormattingApi.Companion {
        override fun getByHexValue(hex: Int): ChatFormatting? =
            MinecraftChatFormatting.getByHexValue(hex)?.run(::ChatFormatting)

        override fun stripFormatting(text: String?): String? =
            MinecraftChatFormatting.stripFormatting(text)

        override fun getByName(name: String): ChatFormatting? =
            MinecraftChatFormatting.getByName(name)?.run(::ChatFormatting)

        override fun getById(id: Int): ChatFormatting? =
            MinecraftChatFormatting.getById(id)?.run(::ChatFormatting)

        override fun getByCode(code: Char): ChatFormatting? =
            MinecraftChatFormatting.getByCode(code)?.run(::ChatFormatting)

        override fun getNames(colors: Boolean, modifiers: Boolean): MutableCollection<String> =
            MinecraftChatFormatting.getNames(colors, modifiers)
    }
}