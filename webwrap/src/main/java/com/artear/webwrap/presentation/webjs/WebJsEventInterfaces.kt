package com.artear.webwrap.presentation.webjs

import android.content.Context
import android.os.Handler
import android.os.Looper

typealias WebJsDispatcher = (data: JSExecutable) -> Unit

fun WebJsDispatcher.dispatch(data: JSExecutable) {
    with(Handler(Looper.getMainLooper())) {
        post { invoke(data) }
    }
}

fun WebJsDispatcher.error(index: Int, message: String?) {
    dispatch(JSExecutable(index, JSCallbackType.ERROR, message))
}

interface EventJs

interface SyncEventJs<T> : EventJs {
    fun event(context: Context, index: Int, data: T): JSExecutable
}

interface DeferEventJs<T> : EventJs {
    fun event(context: Context, delegate: WebJsDispatcher, index: Int, data: T)
}

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
