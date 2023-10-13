package net.warpedvoxels.core.rp.compilation

import net.warpedvoxels.core.rp.dsl.ResourcePackDslMarker
import org.bukkit.NamespacedKey

public data class MinecraftCustomLanguage(val location: NamespacedKey, val value: MutableMap<String, String>) {
    public companion object {
        public val LOCALE_CODE_REGEX: Regex = Regex("[a-z]{2,3}(_[a-z]{2,4})?")
    }
    init {
        require(location.key.matches(LOCALE_CODE_REGEX)) { "Invalid locale code: ${location.key}" }
    }
}

@ResourcePackDslMarker
public inline fun language(location: NamespacedKey, block: MutableMap<String, String>.() -> Unit): MinecraftCustomLanguage =
    MinecraftCustomLanguage(location, mutableMapOf<String, String>().apply(block))

@ResourcePackDslMarker
public inline fun language(namespace: String, key: String, block: MutableMap<String, String>.() -> Unit): MinecraftCustomLanguage =
    MinecraftCustomLanguage(NamespacedKey(namespace, key), mutableMapOf<String, String>().apply(block))

@ResourcePackDslMarker
public inline fun language(location: String, block: MutableMap<String, String>.() -> Unit): MinecraftCustomLanguage =
    MinecraftCustomLanguage(NamespacedKey.fromString(location)!!, mutableMapOf<String, String>().apply(block))
