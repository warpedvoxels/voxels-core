package net.warpedvoxels.core.rp.compilation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.warpedvoxels.core.rp.serialization.FontProviders
import net.warpedvoxels.core.rp.serialization.ResourcePackSerializersModule
import net.warpedvoxels.core.rp.serialization.SoundsEntrypoint
import org.bukkit.Material
import org.bukkit.NamespacedKey
import java.io.File

public data class ResourcePackFileEncoder(val json: Json) {
    @OptIn(ExperimentalSerializationApi::class)
    public companion object {
        public val Default: ResourcePackFileEncoder = ResourcePackFileEncoder(Json {
            encodeDefaults = true
            explicitNulls = false
            serializersModule = ResourcePackSerializersModule
        })
    }
}

public data class ResourcePackCompilationContext(val rootPackFolder: File, val declaration: ResourcePackDeclaration)

public sealed interface ResourcePackFileCreation {
    public val file: File

    public data class New(override val file: File, val content: String) : ResourcePackFileCreation
    public data class Copy(override val file: File, val source: FileProvider) : ResourcePackFileCreation
}

public fun interface ResourcePackCompilationStep {
    public fun compile(enc: ResourcePackFileEncoder, ctx: ResourcePackCompilationContext): Set<ResourcePackFileCreation>
}


/***
 * Alerts if there are unresolved resources.
 */
public object PrepareResourcePackStep : ResourcePackCompilationStep {
    private fun ResourcePackDeclaration.check() {
        val namespacedFiles = externalFiles.map { it.location.toString() }

        val unresolvedBlocks = models.block.values.mapNotNull { it.textures?.allElements }.flatten()
            .filter { it !in namespacedFiles && Material.matchMaterial(it) == null }
        val unresolvedItems = models.item.values.mapNotNull { it.textures?.allElements }.flatten()
            .filter { it !in namespacedFiles && Material.matchMaterial(it) == null }
        val unresolvedSounds = sounds.values.mapNotNull { it.sounds }.flatten()
            .filter { it !in namespacedFiles }

        require(unresolvedBlocks.isEmpty()) { "Unresolved textures in blocks: ${unresolvedBlocks.joinToString { it }}" }
        require(unresolvedItems.isEmpty()) { "Unresolved textures in items: ${unresolvedItems.joinToString { it }}" }
        require(unresolvedSounds.isEmpty()) { "Unresolved sounds: ${unresolvedSounds.joinToString()}" }
    }

    override fun compile(
        enc: ResourcePackFileEncoder,
        ctx: ResourcePackCompilationContext
    ): Set<ResourcePackFileCreation> {
        ctx.declaration.check()
        return emptySet()
    }
}

public object CopyRequiredFilesStep : ResourcePackCompilationStep {
    override fun compile(
        enc: ResourcePackFileEncoder,
        ctx: ResourcePackCompilationContext
    ): Set<ResourcePackFileCreation> {
        val files = ctx.declaration.externalFiles.map {
            val path =
                if (it.type.isNotBlank()) it.targetAssetFile(ctx.rootPackFolder) else it.targetRootFile(ctx.rootPackFolder)
            ResourcePackFileCreation.Copy(path, it.baseFile)
        }
        return files.toSet()
    }
}

public object CompileMetaStep : ResourcePackCompilationStep {
    override fun compile(
        enc: ResourcePackFileEncoder,
        ctx: ResourcePackCompilationContext
    ): Set<ResourcePackFileCreation> {
        val file = File(ctx.rootPackFolder, "pack.mcmeta")
        return setOf(ResourcePackFileCreation.New(file, enc.json.encodeToString(ctx.declaration.meta)))
    }
}

public object CompileSoundsStep : ResourcePackCompilationStep {
    override fun compile(
        enc: ResourcePackFileEncoder,
        ctx: ResourcePackCompilationContext
    ): Set<ResourcePackFileCreation> {
        val namespacedSounds = ctx.declaration.sounds.entries.groupBy { it.key.namespace }
        val files = namespacedSounds.map { (namespace, sounds) ->
            val entrypoint = SoundsEntrypoint(sounds.associate { it.key.toString() to it.value })
            val file = File(ctx.rootPackFolder, "assets/$namespace/sounds.json")
            ResourcePackFileCreation.New(file, enc.json.encodeToString(SoundsEntrypoint.serializer(), entrypoint))
        }
        return files.toSet()
    }
}

public object CompileModelsStep : ResourcePackCompilationStep {
    private inline fun <reified T> compileGeneric(
        enc: ResourcePackFileEncoder,
        ctx: ResourcePackCompilationContext,
        listAccessor: Map<NamespacedKey, T>,
        type: String
    ): Set<ResourcePackFileCreation> {
        val files = listAccessor.map { (location, value) ->
            val file = File(ctx.rootPackFolder, "assets/${location.namespace}/$type/${location.key}.json")
            ResourcePackFileCreation.New(file, enc.json.encodeToString(value))
        }
        return files.toSet()
    }

    override fun compile(
        enc: ResourcePackFileEncoder,
        ctx: ResourcePackCompilationContext
    ): Set<ResourcePackFileCreation> =
        compileGeneric(enc, ctx, ctx.declaration.models.blockState, "blockstates") +
                compileGeneric(enc, ctx, ctx.declaration.models.block, "models/block") +
                compileGeneric(enc, ctx, ctx.declaration.models.item, "models/item")
}

public object CompileFontsStep : ResourcePackCompilationStep {
    override fun compile(
        enc: ResourcePackFileEncoder,
        ctx: ResourcePackCompilationContext
    ): Set<ResourcePackFileCreation> {
        if (ctx.declaration.fonts.isEmpty()) return emptySet()
        val file = File(ctx.rootPackFolder, "assets/minecraft/font/default.json")
        return setOf(ResourcePackFileCreation.New(file, enc.json.encodeToString(FontProviders(ctx.declaration.fonts))))
    }
}

public object CompileLanguageStep : ResourcePackCompilationStep {
    override fun compile(
        enc: ResourcePackFileEncoder,
        ctx: ResourcePackCompilationContext
    ): Set<ResourcePackFileCreation> {
        val files = ctx.declaration.language.map { (location, value) ->
            val file = File(ctx.rootPackFolder, "assets/${location.namespace}/lang/${location.key}.json")
            ResourcePackFileCreation.New(file, enc.json.encodeToString(value))
        }
        return files.toSet()
    }
}

public object CompileParticlesStep : ResourcePackCompilationStep {
    override fun compile(
        enc: ResourcePackFileEncoder,
        ctx: ResourcePackCompilationContext
    ): Set<ResourcePackFileCreation> {
        val files = ctx.declaration.particles.map { (location, value) ->
            val file = File(ctx.rootPackFolder, "assets/${location.namespace}/particles/${location.key}.json")
            ResourcePackFileCreation.New(file, enc.json.encodeToString(value))
        }
        return files.toSet()
    }
}

public object ResourcePackCompiler {
    private val steps: Set<ResourcePackCompilationStep> = setOf(
        PrepareResourcePackStep, CopyRequiredFilesStep, CompileMetaStep, CompileSoundsStep,
        CompileModelsStep, CompileFontsStep, CompileLanguageStep, CompileParticlesStep
    )

    public suspend fun compile(file: File, declaration: ResourcePackDeclaration): CompiledResourcePack {
        val context = ResourcePackCompilationContext(file, declaration)
        val encoder = ResourcePackFileEncoder.Default
        val steps = steps.fold(emptySet<ResourcePackFileCreation>()) { acc, step ->
            acc + step.compile(encoder, context)
        }
        withContext(Dispatchers.IO) {
            for (step in steps) {
                if (!step.file.parentFile.exists() && !step.file.parentFile.mkdirs()) {
                    throw IllegalStateException("Could not create directory ${step.file.parentFile}.")
                }
                when (step) {
                    is ResourcePackFileCreation.New -> step.file.writeText(step.content)
                    is ResourcePackFileCreation.Copy -> step.source.copyTo(step.file)
                }
            }
        }
    }
}

public suspend fun ResourcePackDeclaration.compile(folder: File) {
    if (!folder.exists() && !folder.isDirectory) {
        throw IllegalArgumentException("File $folder is not a directory.")
    }
    return ResourcePackCompiler.compile(folder, this)
}