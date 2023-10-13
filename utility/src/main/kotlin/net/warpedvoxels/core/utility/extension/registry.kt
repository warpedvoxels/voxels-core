package net.warpedvoxels.core.utility.extension

import net.minecraft.core.MappedRegistry
import net.minecraft.core.Registry
import net.minecraft.core.WritableRegistry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import org.bukkit.NamespacedKey
import net.warpedvoxels.core.craftbukkit.CraftServer
import org.bukkit.Bukkit
import java.lang.reflect.Field
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@DslMarker
@Target(AnnotationTarget.FUNCTION)
public annotation class RegistryDsl

@PublishedApi
internal val frozen: Field
    get() = try {
        MappedRegistry::class.java.getDeclaredField("frozen")
            .apply {
                isAccessible = true
            }
    } catch (notFound: NoSuchFieldException) {
        throw IllegalStateException(
            "Cannot unfreeze registry, property not found", notFound
        )
    }

public fun <T> registryOrThrow(key: ResourceKey<out Registry<out T>>): Registry<T> =
    (Bukkit.getServer() as CraftServer).handle.server.registryAccess()
        .registryOrThrow(key)

public fun <V : Any, T : V> Registry<V>.register(
    location: ResourceLocation, entry: T
): T = Registry.register(this, location, entry)

@OptIn(ExperimentalContracts::class)
@RegistryDsl
public inline fun <T, R> Registry<T>.use(block: WritableRegistry<T>.() -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    val writable = this as WritableRegistry<T>
    frozen[writable] = false
    val result = writable.block()
    frozen[writable] = true
    return result
}