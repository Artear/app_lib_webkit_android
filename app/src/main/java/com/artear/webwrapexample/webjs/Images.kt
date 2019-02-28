package com.artear.webwrapexample.webjs

import android.content.Context
import com.artear.injector.api.JsInterface
import com.artear.tools.android.log.logD
import com.artear.webwrap.presentation.webjs.JSCallbackType
import com.artear.webwrap.presentation.webjs.JSExecutable
import com.artear.webwrap.presentation.webjs.SyncEventJs


@JsInterface("images")
class Images : SyncEventJs<ImagesJsData> {

    override fun event(context: Context, index: Int, data: ImagesJsData): JSExecutable {
        logD { "Size images = ${data.items.size}" }
        return JSExecutable(index, JSCallbackType.SUCCESS)
    }
}