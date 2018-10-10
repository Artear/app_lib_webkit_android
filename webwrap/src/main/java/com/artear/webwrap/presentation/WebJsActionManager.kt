package com.artear.webwrap.presentation

import android.webkit.ValueCallback
import android.webkit.WebView
import com.artear.webwrap.*

//@JsActionManager
class WebJsActionManager {

    companion object {
        private const val JAVASCRIPT_INTERFACE_NAME = "Native_"
    }

    val commands: MutableList<CommandJS> = arrayListOf()
    var webView: WebView? = null
    var disposable = SimpleJSDisposable(onDelegate = { executeJS(it) })

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
                "${jsExecutable.type})', ${jsExecutable.data});"))
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


//TODO autogenerar
fun WebJsActionManager.initialize(it: WebView) {
    webView = it

    //cargar esto...
    val eventsJS: MutableList<EventJS> = arrayListOf()

    commands.add(LogJs(it.context, Log(), disposable))
    commands.add(AlertJS(it.context, Alert(), disposable))

    addJavascriptInterfaces(it)
}





