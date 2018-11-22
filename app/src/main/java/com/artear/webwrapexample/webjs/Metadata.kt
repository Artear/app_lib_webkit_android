package com.artear.webwrapexample.webjs

import android.content.Context
import com.artear.injector.api.JsInterface
import com.artear.webwrap.util.log
import com.artear.webwrap.presentation.webjs.JSCallbackType
import com.artear.webwrap.presentation.webjs.JSExecutable
import com.artear.webwrap.presentation.webjs.SyncEventJs

@JsInterface("metadata")
class Metadata : SyncEventJs<MetadataJsData> {

    override fun event(context: Context, index: Int, data: MetadataJsData): JSExecutable {

        log { "SyncEventJs - Metadata - Title = ${data.title}" }
        log { "SyncEventJs - Share - Url = ${data.share.url}" }

        return JSExecutable(index, JSCallbackType.SUCCESS)
    }
}