package net.warpedvoxels.core.rp.upload.service

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import net.minecraft.server.packs.repository.Pack
import net.warpedvoxels.core.rp.compilation.ResourcePackCompiler
import net.warpedvoxels.core.rp.compilation.ResourcePackDeclaration
import net.warpedvoxels.core.rp.upload.DEFAULT_RP_HTTP_CLIENT
import net.warpedvoxels.core.rp.upload.ResourcePackUploadResult
import net.warpedvoxels.core.rp.upload.ResourcePackUploadService
import net.warpedvoxels.core.rp.upload.zip
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.io.path.createTempDirectory
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

@Serializable
@JvmInline
public value class ResourcePackDotHostResultUrl(public val input: String) {
    public companion object {
        public val RESULT_URL_REGEX: Regex = Regex("http://resourcepack\\.host/dl/[^\"]+")
    }

    public val url: String? get() = RESULT_URL_REGEX.find(input)?.groupValues?.getOrNull(0)

    public val ktor: Url get() = Url(url!!)

    init {
        require(url != null) { "Invalid resource pack upload output: $input" }
    }
}

public class ResourcePackDotHostUploadService(
    private val http: HttpClient = DEFAULT_RP_HTTP_CLIENT
) : ResourcePackUploadService {
    public companion object {
        public val EXPIRATION_DURATION: Duration = 90.days
        public const val ENDPOINT: String = "https://resourcepack.host/index.php"
    }

    override suspend fun upload(pack: ResourcePackDeclaration): ResourcePackUploadResult {
        val zipped = pack.zip("resource-pack-dot-host-")
        val response = http.submitFormWithBinaryData(ENDPOINT, formData {
            append("pack", zipped.readBytes(), Headers.build {
                append(HttpHeaders.ContentType, ContentType.Application.Zip)
                append(HttpHeaders.ContentDisposition, "filename=\"${zipped.name}\"")
            })
        })
        require(response.status == HttpStatusCode.OK) {
            "Failed to upload resource pack to 'resourcepack.host' (${response.status}, ${response.body<String>()})."
        }
        val url = response.body<String>()
        return ResourcePackUploadResult(
            ResourcePackDotHostResultUrl(url).ktor, Clock.System.now(), EXPIRATION_DURATION
        )
    }
}