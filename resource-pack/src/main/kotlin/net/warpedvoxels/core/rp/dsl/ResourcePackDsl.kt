package net.warpedvoxels.core.rp.dsl

import net.warpedvoxels.core.rp.compilation.*
import net.warpedvoxels.core.rp.extension.key
import net.warpedvoxels.core.rp.serialization.FontProvider
import net.warpedvoxels.core.rp.serialization.ParticleProvider
import net.warpedvoxels.core.rp.serialization.SoundProvider
import net.warpedvoxels.core.rp.serialization.meta.ResourcePackEntrypoint
import net.warpedvoxels.core.rp.serialization.meta.ResourcePackEntrypointPack
import net.warpedvoxels.core.rp.serialization.model.*
import org.bukkit.Material
import org.bukkit.NamespacedKey
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@DslMarker
internal annotation class ResourcePackDslMarker

@ResourcePackDslMarker
@OptIn(ExperimentalContracts::class)
public data class ResourcePackDsl(
    var meta: ResourcePackEntrypoint = ResourcePackEntrypoint(ResourcePackEntrypointPack()),
    val files: MutableSet<NamespacedFileProvider> = mutableSetOf(),
) {
    private val sound: Sound = Sound()
    private val item: Item = Item()
    private val block: Block = Block()
    private val font: Font = Font()
    private val language: Language = Language()
    private val particle: Particle = Particle()

    public inner class Sound(public var providers: MutableNamespaced<SoundProvider> = mutableMapOf()) {
        public operator fun NamespacedFileProvider.unaryPlus() {
            providers[location] = SoundProvider(sounds = listOf(targetFilePath))
            files.add(this)
        }

        public operator fun NamespacedKey.plus(file: FileProvider) {
            val namespaced = NamespacedFileProvider(file, "sounds", this)
            providers[this] = SoundProvider(sounds = listOf(namespaced.targetFilePath))
            files.add(namespaced)
        }
    }

    public inner class Item(
        public var declarations: MutableSet<ItemDeclaration> = mutableSetOf(),
        private var counter: UInt = 0u
    ) {
        public operator fun ItemDeclaration.plus(textures: Collection<FileProvider>) {
            declarations.add(this)
            files.addAll(textures.map { NamespacedFileProvider(it, "textures/item", identifier) })
        }

        public fun new(
            index: UInt,
            identifier: NamespacedKey,
            baseItem: Material = ItemDeclaration.DEFAULT_BASE_MATERIAL,
            model: ItemModel.(identifier: NamespacedKey) -> Unit
        ): Boolean =
            declarations.add(ItemDeclaration(identifier, baseItem, index, ItemModel().apply { model(identifier) }))

        public fun new(
            index: UInt,
            identifier: String,
            baseItem: Material = ItemDeclaration.DEFAULT_BASE_MATERIAL,
            model: ItemModel.(identifier: NamespacedKey) -> Unit
        ): Boolean = new(index, NamespacedKey.fromString(identifier)!!, baseItem, model)

        public operator fun NamespacedKey.invoke(
            index: UInt = ++counter,
            baseItem: Material = ItemDeclaration.DEFAULT_BASE_MATERIAL,
            model: ItemModel.(identifier: NamespacedKey) -> Unit
        ): Boolean = new(index, this, baseItem, model)

        public operator fun String.invoke(
            index: UInt = ++counter,
            baseItem: Material = ItemDeclaration.DEFAULT_BASE_MATERIAL,
            model: ItemModel.(identifier: NamespacedKey) -> Unit
        ): Boolean = new(index, this, baseItem, model)

        context(ItemModel)
        public infix fun NamespacedKey.pointsTo(path: String): String =
            +resource(path, "textures/item", this)
    }

    public inner class Block(
        public val declarations: MutableSet<BlockDeclaration> = mutableSetOf(),
        private var counter: UInt = 0u
    ) {
        public operator fun BlockDeclaration.plus(textures: Collection<FileProvider>) {
            declarations.add(this)
            files.addAll(textures.map { NamespacedFileProvider(it, "textures/block", identifier) })
        }

        public fun new(
            index: UInt,
            identifier: NamespacedKey,
            itemBaseMaterial: Material? = ItemDeclaration.DEFAULT_BASE_MATERIAL,
            model: BlockModel.(identifier: NamespacedKey) -> Unit
        ): Boolean = declarations.add(
            BlockDeclaration(
                identifier,
                CustomBlockBackendData.NoteBlock(index.toInt()),
                itemBaseMaterial,
                BlockModel().apply { model(identifier) },
                index
            )
        )

        public fun new(
            index: UInt,
            identifier: String,
            itemBaseMaterial: Material? = ItemDeclaration.DEFAULT_BASE_MATERIAL,
            model: BlockModel.(identifier: NamespacedKey) -> Unit
        ): Boolean = new(index, NamespacedKey.fromString(identifier)!!, itemBaseMaterial, model)

        public operator fun NamespacedKey.invoke(
            index: UInt = ++counter,
            itemBaseMaterial: Material? = ItemDeclaration.DEFAULT_BASE_MATERIAL,
            model: BlockModel.(identifier: NamespacedKey) -> Unit
        ): Boolean = new(index, this, itemBaseMaterial, model)

        public operator fun String.invoke(
            index: UInt = ++counter,
            itemBaseMaterial: Material? = ItemDeclaration.DEFAULT_BASE_MATERIAL,
            model: BlockModel.(identifier: NamespacedKey) -> Unit
        ): Boolean = new(index, this, itemBaseMaterial, model)

        context(BlockModel)
        public infix fun NamespacedKey.pointsTo(path: String): String =
            +resource(path, "textures/block", this)
    }

    public inner class Font(public val providers: MutableSet<FontProvider> = mutableSetOf()) {


        public operator fun FontProvider.unaryPlus() {
            providers.add(this)
        }

        public fun bitmap(file: String, height: Int = 8, ascent: Int, chars: List<String>): Boolean =
            providers.add(FontProvider.Bitmap(height, ascent, chars, file))

        public fun bitmap(file: String, height: Int = 8, ascent: Int, char: String): Boolean =
            providers.add(FontProvider.Bitmap(height, ascent, listOf(char), file))

        public fun bitmap(file: NamespacedFileProvider, height: Int = 8, ascent: Int, char: String): Boolean {
            files.add(file)
            return providers.add(FontProvider.Bitmap(height, ascent, listOf(char), file.targetFilePath))
        }

        public fun bitmap(file: NamespacedFileProvider, provider: FontProvider): Boolean {
            files.add(file)
            return providers.add(provider)
        }

        public infix fun NamespacedFileProvider.pointsTo(provider: FontProvider): FontProvider {
            files.add(this)
            val location = if (type.isBlank()) "$location" else "${location.namespace}:$type/${location.key}"
            return when (provider) {
                is FontProvider.Bitmap -> provider.copy(file = location)
                is FontProvider.TTF -> provider.copy(file = location)
                else -> provider
            }
        }

        public infix fun List<String>.overriddenBy(provider: FontProvider) {
            if (provider is FontProvider.Bitmap) {
                +provider.copy(chars = this)
            }
        }
        
        public infix fun String.overriddenBy(provider: FontProvider): Unit = listOf(this) overriddenBy provider
    }

    public inner class Language(public val value: MutableSet<MinecraftCustomLanguage> = mutableSetOf()) {
        public operator fun MinecraftCustomLanguage.unaryPlus(): Boolean = this@Language.value.add(this)
        public operator fun MinecraftCustomLanguage.unaryMinus(): Boolean = this@Language.value.remove(this)

        @ResourcePackDslMarker
        public inline operator fun NamespacedKey.invoke(block: MutableMap<String, String>.() -> Unit): MinecraftCustomLanguage =
            language(this, block)

        @ResourcePackDslMarker
        public inline operator fun String.invoke(block: MutableMap<String, String>.() -> Unit): MinecraftCustomLanguage =
            language(this, block)
    }

    public inner class Particle(public val value: MutableNamespaced<ParticleProvider> = mutableMapOf()) {
        public operator fun set(particleType: org.bukkit.Particle, path: String) {
            value[particleType.key] = ParticleProvider(setOf(path))
        }

        public operator fun set(particleType: org.bukkit.Particle, path: NamespacedFileProvider) {
            files.add(path)
            value[particleType.key] = ParticleProvider(setOf(path.targetFilePath))
        }

        public infix fun org.bukkit.Particle.overriddenBy(path: String): Unit = set(this, path)

        public infix fun org.bukkit.Particle.overriddenBy(path: NamespacedFileProvider): Unit = set(this, path)

        public infix fun NamespacedKey.pointsTo(path: String): String =
            +resource(path, "textures/particle", this)

        public infix fun String.pointsTo(path: String): String =
            +resource(path, "textures/block", this)
    }
    
    public object Resource {
        public fun item(location: String, path: String): NamespacedFileProvider =
            resource(path, "textures/item", location)


        public fun item(path: String, location: NamespacedKey): NamespacedFileProvider =
            resource(path, "textures/item", location)


        public fun item(location: String): NamespacedFileProvider =
            resource(location, "textures/item", location.replace("/", "."))

        public fun block(location: String, path: String): NamespacedFileProvider =
            resource(path, "textures/block", location)


        public fun block(path: String, location: NamespacedKey): NamespacedFileProvider =
            resource(path, "textures/block", location)


        public fun block(location: String): NamespacedFileProvider =
            resource(location, "textures/block", location.replace("/", "."))

        public fun particle(location: String, path: String): NamespacedFileProvider =
            resource(path, "textures/particle", location)


        public fun particle(path: String, location: NamespacedKey): NamespacedFileProvider =
            resource(path, "textures/particle", location)


        public fun particle(location: String): NamespacedFileProvider =
            resource(location, "textures/particle", location.replace("/", "."))

        public fun ui(location: String, path: String): NamespacedFileProvider =
            resource(path, "textures/ui", location)


        public fun ui(path: String, location: NamespacedKey): NamespacedFileProvider =
            resource(path, "textures/ui", location)


        public fun ui(location: String): NamespacedFileProvider =
            resource(location, "textures/ui", location.replace("/", "."))
    }

    public inline val resource: Resource get() = Resource
    
    public operator fun NamespacedFileProvider.unaryPlus(): String {
        files.add(this)
        return "${location.namespace}:$targetFilePath"
    }

    @ResourcePackDslMarker
    public fun sounds(block: Sound.() -> Unit) {
        contract {
            callsInPlace(block, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
        }
        sound.apply(block)
    }

    @ResourcePackDslMarker
    public fun items(block: Item.() -> Unit) {
        contract {
            callsInPlace(block, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
        }
        item.apply(block)
    }

    @ResourcePackDslMarker
    public fun blocks(block: Block.() -> Unit) {
        contract {
            callsInPlace(block, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
        }
        this.block.apply(block)
    }

    @ResourcePackDslMarker
    public fun fonts(block: Font.() -> Unit) {
        contract {
            callsInPlace(block, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
        }
        font.apply(block)
    }

    @ResourcePackDslMarker
    public fun languages(block: Language.() -> Unit) {
        contract {
            callsInPlace(block, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
        }
        language.apply(block)
    }

    @ResourcePackDslMarker
    public fun meta(block: ResourcePackEntrypoint.() -> Unit) {
        contract {
            callsInPlace(block, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
        }
        meta.apply(block)
    }

    @ResourcePackDslMarker
    public fun particles(block: Particle.() -> Unit) {
        contract {
            callsInPlace(block, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
        }
        particle.apply(block)
    }

    private fun addOverride(
        models: MutableNamespaced<ItemModel>, identifier: String,
        baseItem: Material, index: UInt
    ) {
        models.computeIfAbsent(baseItem.key) {
            ItemModel(parent = "minecraft:item/generated").apply {
                textures {
                    layer0 = "$it"
                }
            }
        }.override {
            predicate {
                customModelData = ItemModelPredicate.CustomModelData(index.toInt())
            }
            model = identifier
        }
    }

    public fun build(): ResourcePackDeclaration {
        val blockStateModels = block.declarations.groupBy { it.backendData.baseBlockIdentifier }
            .mapValues { (_, blocks) ->
                val variants = blocks.associate {
                    it.backendData.buildBlockStateVariant("${it.identifier.namespace}:block/${it.identifier.key}")
                }
                BlockStateModel(variants = variants)
            }
        val blockModels = block.declarations.associate { it.identifier to it.model }
        val itemModels = item.declarations.associate { it.identifier to it.model }.toMutableMap()
        for (decl in item.declarations) {
            addOverride(
                itemModels, "${decl.identifier.namespace}:item/${decl.identifier.key}", decl.baseItem,
                decl.index
            )
        }
        for (decl in block.declarations) {
            if (decl.itemBaseMaterial == null) {
                continue
            }
            addOverride(
                itemModels, "${decl.identifier.namespace}:block/${decl.identifier.key}",
                decl.itemBaseMaterial, decl.index + BlockDeclaration.BLOCK_INDEX_OFFSET
            )
        }
        return ResourcePackDeclaration(
            meta = meta, externalFiles = files, sounds = sound.providers, fonts = font.providers,
            language = language.value, particles = particle.value,
            models = ResourcePackDeclaration.Models(blockStateModels, blockModels, itemModels)
        )
    }
}

@OptIn(ExperimentalContracts::class)
@ResourcePackDslMarker
public fun resourcePack(block: ResourcePackDsl.() -> Unit): ResourcePackDeclaration {
    contract {
        callsInPlace(block, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
    }
    return ResourcePackDsl().apply(block).build()
}
