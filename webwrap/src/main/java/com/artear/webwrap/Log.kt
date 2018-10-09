package com.artear.webwrap

import android.content.Context
import android.webkit.JavascriptInterface
import android.widget.Toast
import com.artear.annotations.TestAnnotation
import org.json.JSONException
import org.json.JSONObject

@TestAnnotation(key = "log")
open class Log : EventJS<LogJSData> {

    override fun event(context: Context, index: Int, data: LogJSData) {
        log { "WebJsActionManager - index = $index, log = ${data.message}" }
    }
}

@TestAnnotation(key = "alert")
open class Alert : EventJS<AlertJSData> {

    override fun event(context: Context, index: Int, data: AlertJSData) {
        Toast.makeText(context, data.message, Toast.LENGTH_LONG).show()
    }

}

data class LogJSData(val message: String)

data class AlertJSData(val title: String,
                       val message: String)


//TODO lib
interface EventJS<T> {
    fun event(context: Context, index: Int, data: T)
}

interface CommandJS {
    val key: String
    var context: Context?
    fun execute(index: Int, dataJson: String)
}


//TODO autogenerar

class LogJs(override var context: Context?, private val log: Log) : CommandJS {

    override val key = "log"

    @JavascriptInterface
    override fun execute(index: Int, dataJson: String) {
        context?.apply {
            try {
                val jsonData = JSONObject(dataJson)
                val data = LogJSData(jsonData.getString("message"))
                log.event(this, index, data)
            } catch (exception: JSONException) {
                //log
            }
        }
    }

}

class AlertJS(override var context: Context?, private val alert: Alert) : CommandJS {

    override val key = "alert"

    @JavascriptInterface
    override fun execute(index: Int, dataJson: String) {
        context?.apply {
            try {
                val jsonData = JSONObject(dataJson)
                val data = AlertJSData(jsonData.getString("title"), jsonData.getString("message"))
                alert.event(this, index, data)
            } catch (exception: JSONException) {
                //log
            }
        }
    }

}



