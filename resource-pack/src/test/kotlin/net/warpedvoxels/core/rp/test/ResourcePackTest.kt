package net.warpedvoxels.core.rp.test

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.WordSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.file.shouldExist
import io.kotest.matchers.shouldBe
import net.warpedvoxels.core.rp.compilation.compile
import net.warpedvoxels.core.rp.compilation.resource
import net.warpedvoxels.core.rp.dsl.resourcePack
import net.warpedvoxels.core.rp.serialization.FontProvider
import net.warpedvoxels.core.rp.upload.service.ResourcePackDotHostUploadService
import org.bukkit.Particle
import java.io.File

class ResourcePackTest: WordSpec({
    val rp = resourcePack {
        meta {
            pack {
                description = "My resource pack"
            }
        }
        blocks {
            "test:my_block" {
                parent = "block/cube_all"
                textures {
                    all = it pointsTo "txt/blocks/andesite.png"
                }
            }
        }
        items {
            "test:my_item" {
                parent = "item/handheld"
                textures {
                    layer0 = it pointsTo "txt/items/bone.png"
                }
            }
        }
        languages {
            "test:en_us" {
                set("item.test:my_item", "My Item")
            }
        }
        fonts {
            "[" overriddenBy (resource.item("test:bone_particle", "txt/items/bone.png") pointsTo
                    FontProvider.Bitmap(ascent = 8, height = 8))
            listOf("[", "]") overriddenBy (resource.item("test:bone_particle", "txt/items/bone.png")
                    pointsTo FontProvider.Bitmap(ascent = 8, height = 8))
        }
        particles {
            Particle.WAX_OFF overriddenBy ("test:bone_particle" pointsTo "txt/items/bone.png")
        }
    }
    "A resource pack" should {
        "have a description" {
            rp.meta.pack.description shouldBe "My resource pack"
        }
        "have a block model" {
            rp.models.block("test:my_block")?.parent shouldBe "block/cube_all"
        }
        "compile successfully" {
            val directory = shouldNotThrowAny { tempdir().also { rp.compile(it) } }
            File(directory, "pack.mcmeta").shouldExist()
            File(directory, "assets/test/models/block/my_block.json").shouldExist()
        }
        "upload to resourcepack.host" {
            val service = ResourcePackDotHostUploadService()
            shouldNotThrowAny {
                service.upload(rp)
            }
        }
    }
})