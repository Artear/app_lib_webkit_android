package com.artear.webwrap.presentation.webjs

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.app.AlertDialog
import android.webkit.JavascriptInterface
import android.widget.Toast
import com.artear.annotations.TestAnnotation
import com.artear.webwrap.log
import io.reactivex.Observable
import io.reactivex.Observable.just
import org.json.JSONObject


@TestAnnotation(key = "log")
open class Log : SyncEventJS<LogJSData> {

    override fun event(context: Context, index: Int, data: LogJSData): Observable<JSExecutable> {
        log { "WebJsActionManager - index = $index, log = ${data.message}" }
        return just(JSExecutable(index, JSCallbackType.SUCCESS))
    }

}

@TestAnnotation(key = "alert")
open class Alert : DeferEventJS<AlertJSData> {

    override fun event(context: Context, observer: JsDisposableObserver, index: Int, data: AlertJSData) {
        val builder = AlertDialog.Builder(context)
        builder.setCancelable(true)
        builder.setOnCancelListener {
            Toast.makeText(context, data.message, Toast.LENGTH_LONG).show()
            just(JSExecutable(index, JSCallbackType.SUCCESS)).subscribeWith(observer)
        }

        builder.create().show()
    }

}

data class LogJSData(val message: String)

data class AlertJSData(val title: String,
                       val message: String)

interface EventJS

interface SyncEventJS<T> : EventJS {
    fun event(context: Context, index: Int, data: T): Observable<JSExecutable>
}

interface DeferEventJS<T> : EventJS {
    fun event(context: Context, observer: JsDisposableObserver, index: Int, data: T)
}


interface CommandJS {

    val key: String
    var context: Context?
    var observer: JsDisposableObserver

    fun execute(index: Int, dataJson: String)

    fun getNewObserver(onDelegate: (data: JSExecutable) -> Unit) : JsDisposableObserver{
        observer.dispose()
        return JsDisposableObserver(onDelegate)
    }

    fun clean() {
        observer.dispose()
        context = null
    }
}


//TODO autogenerar

class LogJs(override var context: Context?, private val log: Log,
            var onDelegate: (data: JSExecutable) -> Unit) : CommandJS {

    override lateinit var observer: JsDisposableObserver

    override val key = "log"

    @SuppressLint("CheckResult")
    @JavascriptInterface
    override fun execute(index: Int, dataJson: String) {
        observer = getNewObserver(onDelegate)
        try {
            val jsonData = JSONObject(dataJson)
            val data = LogJSData(jsonData.getString("message"))
            result(log.event(context!!, index, data)).subscribeWith(observer)
        } catch (ex: Exception) {
            error(index, ex.message, observer)
        }
    }

}

class AlertJS(override var context: Context?, private val alert: Alert,
              var onDelegate: (data: JSExecutable) -> Unit) : CommandJS {

    override lateinit var observer: JsDisposableObserver

    override val key = "alert"

    @SuppressLint("CheckResult")
    @JavascriptInterface
    override fun execute(index: Int, dataJson: String) {
        observer = getNewObserver(onDelegate)
        try {
            val jsonData = JSONObject(dataJson)
            val data = AlertJSData(jsonData.getString("title"), jsonData.getString("message"))
            alert.event(context!!, observer, index, data)
        } catch (ex: Exception) {
            error(index, ex.message, observer)
        }
    }

}



