package com.artear.webwrapexample.webjs

import android.content.Context
import com.artear.injector.api.JsInterface
import com.artear.webwrap.log
import com.artear.webwrap.presentation.webjs.JSCallbackType
import com.artear.webwrap.presentation.webjs.JSExecutable
import com.artear.webwrap.presentation.webjs.SyncEventJs


@JsInterface("images")
class Images : SyncEventJs<ImagesJsData> {

    override fun event(context: Context, index: Int, data: ImagesJsData): JSExecutable {
        log {
            "Size images = ${data.images.size}"
        }
        return JSExecutable(index, JSCallbackType.SUCCESS)
    }
}