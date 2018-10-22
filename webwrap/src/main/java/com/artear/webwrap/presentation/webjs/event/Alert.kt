package com.artear.webwrap.presentation.webjs.event

import android.content.Context
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.artear.annotations.JsInterface
import com.artear.webwrap.presentation.webjs.*
import com.artear.webwrap.presentation.webjs.data.AlertJSData

@JsInterface("alert")
class Alert : DeferEventJs<AlertJSData> {

    override fun event(context: Context, delegate: WebJsDispatcher, index: Int, data: AlertJSData) {
        val builder = AlertDialog.Builder(context)
        builder.setCancelable(true)
        builder.setOnCancelListener {
            Toast.makeText(context, data.message, Toast.LENGTH_LONG).show()
            delegate.dispatch(JSExecutable(index, JSCallbackType.SUCCESS, "Alert sent successfully"))
        }
        builder.create().show()
    }
}