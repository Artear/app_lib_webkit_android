package com.artear.webwrap.presentation.webjs.event

import android.content.Context
import com.artear.injector.api.JsInterface
import com.artear.tools.android.log.logD
import com.artear.webwrap.presentation.webjs.JSCallbackType
import com.artear.webwrap.presentation.webjs.JSExecutable
import com.artear.webwrap.presentation.webjs.SyncEventJs

@JsInterface("log")
open class Log : SyncEventJs<LogJsData> {

    override fun event(context: Context, index: Int, data: LogJsData): JSExecutable {
        logD { "EventJS - Log - index = $index, log = ${data.message}" }
        return JSExecutable(index, JSCallbackType.SUCCESS)
    }

}