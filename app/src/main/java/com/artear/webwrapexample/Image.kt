package com.artear.webwrapexample

import android.content.Context
import com.artear.injector.api.JsInterface
import com.artear.webwrap.presentation.webjs.JSCallbackType
import com.artear.webwrap.presentation.webjs.JSExecutable
import com.artear.webwrap.presentation.webjs.SyncEventJs


@JsInterface("image")
class Image : SyncEventJs<ImageJsData> {

    override fun event(context: Context, index: Int, data: ImageJsData): JSExecutable {
        return JSExecutable(index, JSCallbackType.SUCCESS)
    }
}