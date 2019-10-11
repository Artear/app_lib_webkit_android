package com.artear.webwrap.presentation.webjs

import android.webkit.ValueCallback
import android.webkit.WebView
import com.artear.injector.api.JsEventManager
import com.artear.injector.api.JsInterface
import com.artear.webwrap.WebWrapper

/**
 * A web javascript event manager act like a mediator among your application and your [WebView].
 * Establish a communication between a [CommandJs] and each javascript event that you want to
 * respond.
 *
 * The [Injector Library](https://github.com/Artear/app_lib_injector_android) provides an extra
 * functionality to [WebWrapper]. It's a annotation processor that generate an extension to
 * this library creating all [CommandJs] with [JsInterface].
 *
 * You must to extend from [EventJs] //TODO
 *
 * [JsEventManager] is an annotation that provide an initialization for that [CommandJs].
 *
 *
 * @see WeJS
 */
@JsEventManager
class WebJsEventManager {

    companion object {
        private const val JAVASCRIPT_INTERFACE_NAME = "Native_"
    }

    val commands: MutableList<CommandJs> = arrayListOf()
    var webView: WebView? = null

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






