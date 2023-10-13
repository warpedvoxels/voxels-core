
@file:JvmName("VelocityCoroutinesContinuationAdapter")
@file:Suppress("UnstableApiUsage")
package net.warpedvoxels.proxy.core.coroutines

import com.google.common.reflect.TypeToken
import com.velocitypowered.api.event.EventManager
import com.velocitypowered.api.event.EventTask
import net.warpedvoxels.proxy.core.VelocityModule
import java.lang.reflect.Method
import java.util.function.BiConsumer
import java.util.function.BiFunction
import java.util.function.Function
import java.util.function.Predicate
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.reflect.jvm.kotlinFunction
import com.velocitypowered.api.event.Continuation as EventContinuation

public fun suspendingEventTask(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend () -> Unit
): EventTask =
    EventTask.withContinuation { cont ->
        block.startCoroutine(cont.asCoroutineContinuation(context))
    }

public fun EventContinuation.asCoroutineContinuation(
    context: CoroutineContext = EmptyCoroutineContext
): Continuation<Unit> = Continuation(context) { result ->
    if (result.isFailure) {
        resumeWithException(result.exceptionOrNull())
    } else {
        resume()
    }
}

internal fun VelocityModule.registerCoroutineContinuationAdapter() {
    try {
        eventManager.registerHandlerAdapter(
            name = "kt_suspend",
            filter = filter@{ method ->
                method.kotlinFunction != null &&
                        method.kotlinFunction!!.isSuspend
            },
            validator = { method, errors ->
                val function = method.kotlinFunction!!
                // parameters includes receiver, but excludes continuation
                if (function.parameters.size != 2) {
                    errors.add("function must have a single parameter which is the event type")
                }
                if (function.returnType.classifier != Unit::class) {
                    errors.add("function return type must be Unit")
                }
            },
            invokeFunctionType = object : TypeToken<suspend (Any, Any) -> Unit>() {},
            handlerBuilder = { invokeFunction ->
                BiFunction { instance, event ->
                    suspendingEventTask {
                        invokeFunction(instance, event)
                    }
                }
            }
        )
    } catch (ex: UnsupportedOperationException) {
        logger.warn("Suspending event functions will not be supported.", ex)
    }
}

private fun <F> EventManager.registerHandlerAdapter(
    name: String,
    filter: Predicate<Method>,
    validator: BiConsumer<Method, MutableList<String>>,
    invokeFunctionType: TypeToken<F>,
    handlerBuilder: Function<F, BiFunction<Any, Any, EventTask>>
) {
    try {
        val method = javaClass.getMethod(
            "registerHandlerAdapter",
            String::class.java, Predicate::class.java,
            BiConsumer::class.java, TypeToken::class.java,
            Function::class.java
        )
        method.invoke(
            this,
            name,
            filter,
            validator,
            invokeFunctionType,
            handlerBuilder
        )
    } catch (ex: NoSuchMethodException) {
        throw UnsupportedOperationException(
            "The registerHandlerAdapter method couldn't be found in " +
                    "VelocityEventManager, handler adapters aren't supported.",
            ex
        )
    }
}