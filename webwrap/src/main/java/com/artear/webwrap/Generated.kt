package com.artear.webwrap

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.artear.webwrap.presentation.webjs.*
import com.artear.webwrap.presentation.webjs.event.Alert
import com.artear.webwrap.presentation.webjs.event.AlertJSData
import com.artear.webwrap.presentation.webjs.event.Log
import com.artear.webwrap.presentation.webjs.event.LogJSData
import org.json.JSONObject


//TODO autogenerar
fun WebJsEventManager.initialize(it: WebView) {
    webView = it

    //cargar esto...
    val eventsJS: MutableList<EventJs> = arrayListOf()

    val delegate: WebJsDispatcher = { executeJS(it) }

    commands.add(LogJs(it.context, Log(), delegate))
    commands.add(AlertJs(it.context, Alert(), delegate))

    addJavascriptInterfaces(it)
}

class LogJs(override var context: Context?, private val log: Log,
            override var delegate: WebJsDispatcher?) : CommandJs {

    override val key = "log"

    @SuppressLint("CheckResult")
    @JavascriptInterface
    override fun execute(index: Int, dataJson: String) {
        try {
            val jsonData = JSONObject(dataJson)
            val data = LogJSData(jsonData.getString("message"))
            val event = log.event(context!!, index, data)
            delegate!!.dispatch(event)
        } catch (ex: Exception) {
            delegate?.error(index, ex.message)
        }
    }

}

class AlertJs(override var context: Context?, private val alert: Alert,
              override var delegate: WebJsDispatcher?) : CommandJs {

    override val key = "alert"

    @SuppressLint("CheckResult")
    @JavascriptInterface
    override fun execute(index: Int, dataJson: String) {
        try {
            val jsonData = JSONObject(dataJson)
            val data = AlertJSData(jsonData.getString("title"), jsonData.getString("message"))
            alert.event(context!!, delegate!!, index, data)
        } catch (ex: Exception) {
            delegate?.error(index, ex.message)
        }
    }

}



