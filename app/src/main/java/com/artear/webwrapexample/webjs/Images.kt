package com.artear.webwrapexample.webjs

import android.content.Context
import com.artear.injector.api.JsInterface
import com.artear.webwrap.util.log
import com.artear.webwrap.presentation.webjs.JSCallbackType
import com.artear.webwrap.presentation.webjs.JSExecutable
import com.artear.webwrap.presentation.webjs.SyncEventJs


@JsInterface("images")
class Images : SyncEventJs<ImagesJsData> {

    override fun event(context: Context, index: Int, data: ImagesJsData): JSExecutable {
        log {
            "Size images = ${data.items.size}"
        }
        return JSExecutable(index, JSCallbackType.SUCCESS)
    }
}