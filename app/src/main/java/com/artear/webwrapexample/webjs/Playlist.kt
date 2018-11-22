package com.artear.webwrapexample.webjs

import android.content.Context
import com.artear.injector.api.JsInterface
import com.artear.webwrap.util.log
import com.artear.webwrap.presentation.webjs.JSCallbackType
import com.artear.webwrap.presentation.webjs.JSExecutable
import com.artear.webwrap.presentation.webjs.SyncEventJs

@JsInterface("playlist")
class Playlist : SyncEventJs<PlaylistJsData> {

    override fun event(context: Context, index: Int, data: PlaylistJsData): JSExecutable {
        try {
            log { "First video source = ${data.items.first().source} " }
        } catch (ex: NoSuchElementException) {
            return JSExecutable(index, JSCallbackType.ERROR)
        }
        return JSExecutable(index, JSCallbackType.SUCCESS)
    }
}