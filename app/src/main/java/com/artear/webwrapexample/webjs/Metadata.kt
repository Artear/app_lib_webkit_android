package com.artear.webwrapexample.webjs

import android.content.Context
import com.artear.injector.api.JsInterface
import com.artear.tools.android.log.logD
import com.artear.webwrap.presentation.webjs.JSCallbackType
import com.artear.webwrap.presentation.webjs.JSExecutable
import com.artear.webwrap.presentation.webjs.SyncEventJs

@JsInterface("metadata")
class Metadata : SyncEventJs<MetadataJsData> {

    override fun event(context: Context, index: Int, data: MetadataJsData): JSExecutable {

        logD { "SyncEventJs - Metadata - Title = ${data.title}" }
        logD { "SyncEventJs - Share - Url = ${data.share.url}" }

        return JSExecutable(index, JSCallbackType.SUCCESS)
    }
}