package net.warpedvoxels.core.rp.upload

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import net.warpedvoxels.core.rp.compilation.ResourcePackCompiler
import net.warpedvoxels.core.rp.compilation.ResourcePackDeclaration
import java.io.BufferedOutputStream
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.io.path.createTempDirectory
import kotlin.time.Duration

internal val DEFAULT_RP_HTTP_CLIENT: HttpClient by lazy {
    HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }
}

public data class ResourcePackUploadResult(
    val directUrl: Url,
    val uploadTimestamp: Instant,
    val expirationDuration: Duration,
) {
    public val hasExpired: Boolean get() = Clock.System.now() - uploadTimestamp > expirationDuration
}

public fun interface ResourcePackUploadService {
    public suspend fun upload(pack: ResourcePackDeclaration): ResourcePackUploadResult
}

internal suspend fun ResourcePackDeclaration.zip(tempPrefix: String): File {
    val input = createTempDirectory(tempPrefix).toFile().also {
        ResourcePackCompiler.compile(it, this)
    }
    return kotlin.io.path.createTempFile("zip-$tempPrefix", ".zip").toFile().also { output ->
        ZipOutputStream(BufferedOutputStream(output.outputStream())).use { stream ->
            for (file in input.walkTopDown()) {
                val relativePath = file.toRelativeString(input)
                stream.putNextEntry(ZipEntry(relativePath + if (file.isDirectory) "/" else ""))
                if (file.isFile) {
                    file.inputStream().use { stream2 -> stream2.copyTo(stream) }
                }
            }
        }
    }
}