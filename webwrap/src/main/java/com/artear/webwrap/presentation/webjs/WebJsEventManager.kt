package com.artear.webwrap.presentation.webjs

import android.webkit.ValueCallback
import android.webkit.WebView
import com.artear.annotations.JsEventManager
import com.artear.webwrap.presentation.webjs.event.Alert
import com.artear.webwrap.presentation.webjs.event.AlertJs
import com.artear.webwrap.presentation.webjs.event.Log
import com.artear.webwrap.presentation.webjs.event.LogJs

@JsEventManager
class WebJsEventManager {

    companion object {
        private const val JAVASCRIPT_INTERFACE_NAME = "Native_"
    }

    val commands: MutableList<CommandJs> = arrayListOf()
    var webView: WebView? = null

    fun initialize(it: WebView) {
        webView = it

        //cargar esto...
        val eventsJS: MutableList<EventJs> = arrayListOf()

        val delegate: WebJsDispatcher = { executeJS(it) }

        commands.add(LogJs(it.context, Log(), delegate))
        commands.add(AlertJs(it.context, Alert(), delegate))

        addJavascriptInterfaces(it)
    }

    fun addJavascriptInterfaces(webView: WebView) {
        commands.forEach {
            webView.addJavascriptInterface(it, JAVASCRIPT_INTERFACE_NAME.plus(it.key))
        }
    }

    fun removeJavascriptInterfaces(webView: WebView) {
        commands.forEach {
            webView.removeJavascriptInterface(JAVASCRIPT_INTERFACE_NAME.plus(it.key))
        }
    }

    fun removeAllCommands() {
        commands.forEach { it.clean() }
        commands.clear()
    }

    fun executeJS(jsExecutable: JSExecutable) {
        evaluateJavaScript("Manager.callback(${jsExecutable.index},".plus(
                "\"${jsExecutable.type}\", ${jsExecutable.data});"))
    }

    fun refreshIframe() {
        evaluateJavaScript("Manager.refreshIframe()")
    }

    private fun evaluateJavaScript(script: String, callback: ValueCallback<String>? = null) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            webView?.evaluateJavascript(script, callback)
        } else {
            webView?.loadUrl("javascript:$script")
        }
    }

}






