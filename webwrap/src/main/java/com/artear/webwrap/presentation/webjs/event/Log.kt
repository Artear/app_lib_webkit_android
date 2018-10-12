package com.artear.webwrap.presentation.webjs.event

import android.content.Context
import com.artear.annotations.JsInterface
import com.artear.webwrap.log
import com.artear.webwrap.presentation.webjs.JSCallbackType
import com.artear.webwrap.presentation.webjs.JSExecutable
import com.artear.webwrap.presentation.webjs.SyncEventJs

data class LogJSData(val message: String)

@JsInterface(key = "log")
open class Log : SyncEventJs<LogJSData> {

    override fun event(context: Context, index: Int, data: LogJSData): JSExecutable {
        log { "EventJS - Log - index = $index, log = ${data.message}" }
        return JSExecutable(index, JSCallbackType.SUCCESS)
    }

}