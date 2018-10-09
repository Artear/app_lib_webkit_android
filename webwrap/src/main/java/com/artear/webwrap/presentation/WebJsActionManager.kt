package com.artear.webwrap.presentation

import android.content.Context
import android.webkit.WebView
import com.artear.webwrap.*

//@JsActionManager
class WebJsActionManager(val context: Context) {

    companion object {
        private const val JAVASCRIPT_INTERFACE_NAME = "Native_"

    }

    val commands: MutableList<CommandJS> = arrayListOf()

    fun addJavascriptInterfaces(webView: WebView) {
        commands.forEach {
            webView.addJavascriptInterface(it, JAVASCRIPT_INTERFACE_NAME + it.key)
        }
    }

    fun removeJavascriptInterfaces(webView: WebView) {
        commands.forEach {
            webView.removeJavascriptInterface(JAVASCRIPT_INTERFACE_NAME + it.key)
        }
    }

    fun removeAllCommands() {
        commands.forEach { it.context = null }
        commands.clear()
    }
}


//TODO autogenerar
fun WebJsActionManager.initialize() {

    //cargar esto...

    val eventsJS: MutableList<EventJS<*>> = arrayListOf()


    commands.add(LogJs(context, Log()))
    commands.add(AlertJS(context, Alert()))

}





