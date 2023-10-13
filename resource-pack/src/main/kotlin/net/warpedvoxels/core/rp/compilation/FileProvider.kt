package net.warpedvoxels.core.rp.compilation

import org.bukkit.NamespacedKey
import java.io.File

public sealed interface FileProvider {
    public fun exists(): Boolean

    public val extension: String

    public fun copyTo(target: File): Boolean
}

@JvmInline
public value class DefaultFileProvider(public val file: File) : FileProvider {
    override fun exists(): Boolean = file.exists()

    override fun copyTo(target: File): Boolean {
        if (!target.parentFile.exists() && !target.parentFile.mkdirs()) return false
        return file.copyTo(target, overwrite = true).exists()
    }

    override val extension: String
        get() = file.extension.takeIf { it.isNotEmpty() }?.let { ".$it" } ?: ""
}

@JvmInline
public value class ProjectResourceFileProvider(public val path: String) : FileProvider {
    init {
        require(path.isNotEmpty()) { "Resource path cannot be empty." }
    }

    private val classResource get() = Thread.currentThread().contextClassLoader

    override fun exists(): Boolean = classResource.getResource(path) != null

    override val extension: String
        get() = path.substringAfterLast('.', "")
            .takeIf { it.isNotEmpty() }?.let { ".$it" } ?: ""

    override fun copyTo(target: File): Boolean {
        if (!target.parentFile.exists() && !target.parentFile.mkdirs()) return false
        return classResource.getResourceAsStream(path)?.use { input ->
            target.outputStream().use { output ->
                input.copyTo(output)
            }
            true
        } ?: false
    }
}

public class InlineFileProvider(override val extension: String, public val contents: String): FileProvider {
    override fun exists(): Boolean {
        return true
    }

    override fun copyTo(target: File): Boolean {
        target.writeText(contents)
        return true
    }
}

public data class NamespacedFileProvider(val baseFile: FileProvider, val type: String, val location: NamespacedKey) {
    internal val targetFilePath: String = location.key.replace('.', File.separatorChar)
    public fun targetRootFile(rootFolder: File): File =
        File(rootFolder, targetFilePath)
    public fun targetAssetFile(rootFolder: File): File =
        File(rootFolder, "assets/${location.namespace}/${type.replace('/', File.separatorChar)}/$targetFilePath${baseFile.extension}")
}

public fun resource(path: String, type: String, location: NamespacedKey): NamespacedFileProvider =
    NamespacedFileProvider(ProjectResourceFileProvider(path), type, location)


public fun resource(path: String, type: String, location: String): NamespacedFileProvider =
    NamespacedFileProvider(ProjectResourceFileProvider(path), type, NamespacedKey.fromString(location)!!)

public fun file(file: File, type: String, location: NamespacedKey): NamespacedFileProvider =
    NamespacedFileProvider(DefaultFileProvider(file), type, location)


public fun file(file: File, type: String, location: String): NamespacedFileProvider =
    NamespacedFileProvider(DefaultFileProvider(file), type, NamespacedKey.fromString(location)!!)