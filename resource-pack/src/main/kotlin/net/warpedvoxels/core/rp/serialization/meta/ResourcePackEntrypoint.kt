package net.warpedvoxels.core.rp.serialization.meta

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.warpedvoxels.core.rp.dsl.ResourcePackDslMarker
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Minecraft identifies a resource pack based on the presence of the file `pack.mcmeta` in the root directory.
 *
 * @param pack     The pack object contains information about the pack itself.
 * @param language The language object contains additional languages to be added to the language dropdown
 *                 on the "Options" screen.
 * @param filter   The filter object contains information about which files shouldn't be loaded from the pack.
 *                 This means those files are going to be treated as if they didn't exist.
 */
@Serializable
@SerialName("pack.mcmeta")
public data class ResourcePackEntrypoint(
    var pack: ResourcePackEntrypointPack,
    var language: Map<String, ResourcePackEntrypointLanguage>? = null,
    var filter: ResourcePackEntrypointFilter? = null,
) {
    @ResourcePackDslMarker
    @OptIn(ExperimentalContracts::class)
    public fun pack(block: ResourcePackEntrypointPack.() -> Unit) {
        contract {
            callsInPlace(block, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
        }
        pack.apply(block)
    }

    @ResourcePackDslMarker
    @OptIn(ExperimentalContracts::class)
    public fun filter(block: ResourcePackEntrypointFilter.() -> Unit) {
        contract {
            callsInPlace(block, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
        }
        filter = ResourcePackEntrypointFilter().apply(block)
    }
}

/**
 * The pack object contains information about the pack itself.
 *
 * @param description A short description of the pack.
 * @param format The resource pack format version.
 */
@Serializable
@SerialName("pack")
public data class ResourcePackEntrypointPack(
    @SerialName("pack_format")
    var format: ResourcePackFormat = ResourcePackFormat.v1_20_x,
    var description: String? = "§7Resource pack generated with voxels-core.\n§fgithub.com/warpedvoxels"
)

/**
 * The language object contains additional languages to be added to the language dropdown on the "Options" screen.
 *
 * @param name The full name of the language.
 * @param region The country or region the language is primarily spoken in.
 * @param bidirectional Whether the language is written from right to left.
 */
@Serializable
@SerialName("language")
public data class ResourcePackEntrypointLanguage(
    var name: String,
    var region: String,
    var bidirectional: Boolean = false
)

/**
 * The filter object contains information about which files shouldn't be loaded from the pack.
 * This means those files are going to be treated as if they didn't exist.
 *
 * @param block List of patterns.
 */
@Serializable
@SerialName("filter")
public data class ResourcePackEntrypointFilter(
    var block: List<ResourcePackEntrypointFilterPattern> = emptyList()
)

/**
 * A pattern that is used to filter files from the pack.
 *
 * @param namespace A regular expression for the namespace of files to be filtered out.
 *                  If unspecified, it applies to every namespace.
 * @param path      A regular expression for the path of files to be filtered out.
 *                  If unspecified, it applies to every file.
 */
@Serializable
public data class ResourcePackEntrypointFilterPattern(
    var namespace: String = ".*",
    var path: String = ".*"
)