package com.artear.webwrap

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.JavascriptInterface
import android.widget.Toast
import com.artear.annotations.TestAnnotation
import com.artear.webwrap.presentation.JSCallbackType
import com.artear.webwrap.presentation.JSExecutable
import com.artear.webwrap.presentation.SimpleJSDisposable
import io.reactivex.Observable
import org.json.JSONObject


@TestAnnotation(key = "log")
open class Log : SimpleEventJS<LogJSData> {

    override fun event(context: Context, index: Int, data: LogJSData): Observable<JSExecutable> {
        log { "WebJsActionManager - index = $index, log = ${data.message}" }
        return Observable.just(JSExecutable(index, JSCallbackType.SUCCESS))
    }

}

@TestAnnotation(key = "alert")
open class Alert : DeferEventJS<AlertJSData> {

    override fun event(context: Context, disposable: SimpleJSDisposable, index: Int, data: AlertJSData) {
        Toast.makeText(context, data.message, Toast.LENGTH_LONG).show()
    }

}

data class LogJSData(val message: String)

data class AlertJSData(val title: String,
                       val message: String)

interface EventJS

interface SimpleEventJS<T> : EventJS {
    fun event(context: Context, index: Int, data: T): Observable<JSExecutable>
}

interface DeferEventJS<T> : EventJS {
    fun event(context: Context, disposable: SimpleJSDisposable, index: Int, data: T)
}


interface CommandJS {

    val key: String
    var context: Context?
    var subscription: SimpleJSDisposable

    fun execute(index: Int, dataJson: String)

    @SuppressLint("CheckResult")
    fun error(index: Int, message: String?, disposable: SimpleJSDisposable) {
        Observable.error<JSExecutable>(JSExecuteException(index, message)).subscribeWith(disposable)
    }

    fun clean() {
        subscription.dispose()
        context = null
    }
}


//TODO autogenerar

class LogJs(override var context: Context?, private val log: Log,
            private val disposable: SimpleJSDisposable) : CommandJS {

    override val key = "log"
    override lateinit var subscription: SimpleJSDisposable

    @JavascriptInterface
    override fun execute(index: Int, dataJson: String) {
        try {
            val jsonData = JSONObject(dataJson)
            val data = LogJSData(jsonData.getString("message"))
            subscription = log.event(context!!, index, data).subscribeWith(disposable)
        } catch (ex: Exception) {
            error(index, ex.message, disposable)
        }
    }

}

class AlertJS(override var context: Context?, private val alert: Alert,
              private val disposable: SimpleJSDisposable) : CommandJS {

    override val key = "alert"
    override lateinit var subscription: SimpleJSDisposable

    @SuppressLint("CheckResult")
    @JavascriptInterface
    override fun execute(index: Int, dataJson: String) {
        try {
            val jsonData = JSONObject(dataJson)
            val data = AlertJSData(jsonData.getString("title"), jsonData.getString("message"))
            alert.event(context!!, disposable, index, data)
        } catch (ex: Exception) {
            error(index, ex.message, disposable)
        }
    }

}



