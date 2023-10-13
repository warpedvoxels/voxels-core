@file:JvmName("PaperMultiVersionApi")

package net.warpedvoxels.core.multiversion.api

import org.bukkit.Bukkit
import org.bukkit.Server

public fun Server.`package`(): String = this::class.java.`package`.name

/**
 * Returns the version of the server as a string, e.g. 1.16.5 -> 1_16_R3.
 */
public fun extractServerVersion(server: Server = Bukkit.getServer()): String =
    server.`package`().substring(server.`package`().lastIndexOf('.') + 1)
