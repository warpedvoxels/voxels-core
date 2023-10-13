@file:JvmName("PaperMultiVersionApiTest")

package net.warpedvoxels.core.multiversion.api

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.bukkit.Bukkit
import org.bukkit.Server

class PaperMultiVersionApiTest: WordSpec({
    val server = mockk<Server>()
    mockkStatic("net.warpedvoxels.core.multiversion.api.PaperMultiVersionApi", "org.bukkit.Bukkit")
    beforeTest {
        clearMocks(server)
        every { Bukkit.getServer() } returns server
    }

    "`extractServerVersion`" When {
        "the server software version is 1.20.1" should {
            "return v1_20_R1" {
                every { server.`package`() } returns "org.bukkit.craftbukkit.v1_20_R1"
                extractServerVersion() shouldBe "v1_20_R1"
            }
        }
        "the server software version is 1.16.5" should {
            "return v1_16_R3" {
                every { server.`package`() } returns "org.bukkit.craftbukkit.v1_16_R3"
                extractServerVersion() shouldBe "v1_16_R3"
            }
        }
    }
})