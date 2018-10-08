package com.artear.webwrap.presentation

import android.content.Context
import android.webkit.JavascriptInterface
import android.widget.Toast


class WebJsActionManager( val context: Context) {

    @JavascriptInterface
    fun execute(callback: Int, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        com.artear.webwrap.log { "WebJsActionManager - callback = $callback, log = $message" }
    }
}

class WebJsActionManager2( val context: Context) {

    @JavascriptInterface
    fun execute(callback: Int, data: String) {
        Toast.makeText(context, data, Toast.LENGTH_LONG).show()
        com.artear.webwrap.log { "WebJsActionManager - callback = $callback, log = $data" }
    }
}



