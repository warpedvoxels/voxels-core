package net.warpedvoxels.core.compose.hud.shader

import net.kyori.adventure.text.format.TextColor
import net.warpedvoxels.core.rp.compilation.InlineFileProvider
import net.warpedvoxels.core.rp.compilation.NamespacedFileProvider

public object HudAnchoringShader : CoreShader {
    override val type: CoreShaderType = CoreShaderType.RenderTypeText

    private val vertex: String = """
        #version 150

        #moj_import <fog.glsl>

        in vec3 Position;
        in vec4 Color;
        in vec2 UV0;
        in ivec2 UV2;

        uniform sampler2D Sampler2;

        uniform mat4 ModelViewMat;
        uniform mat4 ProjMat;
        uniform mat3 IViewRotMat;
        uniform int FogShape;

        out float vertexDistance;
        out vec4 vertexColor;
        out vec2 texCoord0;

        float offsets[13] = float[13](
            0., //0
            -2., //1
            -1.5, //2
            -1.25, //3
            -1., //4
            -0.5, //5
            -0.25, //6
            .25, //7
            .5, //8
            1., //9
            1.25, //A
            1.5, //B
            2. //C
        );

        ivec2 MARKER_COLOR = ivec2(168, 28); // **a81c
        ivec2 SHADOW_COLOR = ivec2(168 / 4, 28 / 4);

        void main() {
            ivec2 green_blue_channels = ivec2(Color.gb * 255. + 0.5);

            if (green_blue_channels == SHADOW_COLOR) {
                gl_Position = vec4(3.0, 3.0, 3.0, 1.0);
                return;
            }

            int color = int(Color.r * 255.); // The red channel is composed by the first
                                             // two characters in the hexadecimal color notation.

            // Split into two 4-bit values.
            // offset[0]: move the high 4 bits to the right and isolate them
            // offset[1]: mask the low 4 bits
            ivec2 offset = ivec2((color >> 4) & 0xf, color & 0xf);

            bool is_offset_marker = green_blue_channels == MARKER_COLOR
                && offset.x <= offsets.length()
                && offset.y <= offsets.length();
            if (is_offset_marker) {
                gl_Position = ProjMat * ModelViewMat * vec4(Position.xy, Position.z - 2.0, 1.0);
                gl_Position.xy += gl_Position.w * vec2(offsets[offset.x], offsets[offset.y]);
            } else {
                gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
            }

            vertexDistance = fog_distance(ModelViewMat, IViewRotMat * Position, FogShape);
            vertexColor = texelFetch(Sampler2, UV2 / 16, 0);
            if (!is_offset_marker)
                vertexColor *= Color;
            texCoord0 = UV0;
        }
    """.trimIndent()

    // HUD anchoring shader compatibility should be written by other modules.
    override fun combine(other: CoreShader): CoreShader? = null

    override val providers: List<NamespacedFileProvider> = listOf(
        NamespacedFileProvider(InlineFileProvider(".vsh", vertex), "shaders/core", type.key)
    )

    @OptIn(ExperimentalStdlibApi::class)
    public fun color(first: Int, second: Int): TextColor =
        TextColor.fromHexString("#" + first.toHexString() + second.toHexString() + "a81c")!!
}