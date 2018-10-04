package com.artear.webwrap.presentation

import android.content.Context
import android.webkit.JavascriptInterface
import android.widget.Toast
import com.artear.webwrap.log


class WebJsActionManager(private val context: Context) {

    @JavascriptInterface
    fun log(callback: Int, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        log { "WebJsActionManager - callback = $callback, log = $message" }
    }

}
