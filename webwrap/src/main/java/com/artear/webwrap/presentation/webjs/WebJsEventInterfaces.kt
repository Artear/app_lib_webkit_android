package com.artear.webwrap.presentation.webjs

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.squareup.moshi.JsonClass

/**
 * A type alias for a dispatcher delegate function for javascript events. Receive a
 * [JSExecutable] and return a [Unit].
 */
typealias WebJsDispatcher = (data: JSExecutable) -> Unit

/**
 * An extension function to dispatch the result of an execution with a [JSExecutable].
 *
 * Note: independently of thread execution this function will be dispatched on **Main Thread**.
 */
fun WebJsDispatcher.dispatch(data: JSExecutable) {
    with(Handler(Looper.getMainLooper())) {
        post { invoke(data) }
    }
}

/**
 * An extension function error that call [dispatch] with a [JSExecutable] error and the message
 * error.
 *
 */
fun WebJsDispatcher.error(index: Int, message: String?) {
    dispatch(JSExecutable(index, JSCallbackType.ERROR, message))
}

/**
 * Main interface for a javascript event. Can be a [SyncEventJs] or [DeferEventJs].
 */
interface EventJs

/**
 * Each class that implements this must to have the annotation [JsonClass]
 * with property generateAdapter = true
 */
interface EventJsData

/**
 * Synchronize javascript event. Execute and return a [JSExecutable]
 */
interface SyncEventJs<T : EventJsData> : EventJs {
    fun event(context: Context, index: Int, data: T): JSExecutable
}

/**
 * A defer javascript event. Execute and send a delegate [WebJsDispatcher] to respond when
 * finish the execution.
 */
interface DeferEventJs<T : EventJsData> : EventJs {
    fun event(context: Context, delegate: WebJsDispatcher, index: Int, data: T)
}

/**
 * //TODO!!!
 */
interface CommandJs {
    val key: String
    var context: Context?
    var delegate: WebJsDispatcher?

    fun execute(index: Int, dataJson: String)

    fun clean() {
        delegate = null
        context = null
    }
}
