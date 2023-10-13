package net.warpedvoxels.proxy.core

import com.google.inject.Inject
import com.velocitypowered.api.event.EventManager
import com.velocitypowered.api.event.PostOrder
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.proxy.ProxyServer
import net.warpedvoxels.core.command.velocity.commands
import net.warpedvoxels.proxy.core.command.TestCommand
import org.slf4j.Logger

@Plugin(id = "voxels-proxy-core")
class ExampleProxyCorePlugin @Inject constructor(
    pc: PluginContainer, l: Logger, em: EventManager, ps: ProxyServer,
) {
    val module = VelocityModule("example-core", this, l, pc, em, ps)

    @Subscribe(order = PostOrder.FIRST)
    fun initialise(event: ProxyInitializeEvent): Unit = module.init {
        commands {
            install(TestCommand())
        }
    }

    @Subscribe(order = PostOrder.FIRST)
    fun shutdown(event: ProxyShutdownEvent): Unit = module.finalise {

    }
}
