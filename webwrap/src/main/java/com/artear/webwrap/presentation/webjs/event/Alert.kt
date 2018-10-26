package com.artear.webwrap.presentation.webjs.event

import android.content.Context
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.artear.injector.api.JsInterface
import com.artear.webwrap.presentation.webjs.*

@JsInterface("alert")
class Alert : DeferEventJs<AlertJsData> {

    override fun event(context: Context, delegate: WebJsDispatcher, index: Int, data: AlertJsData) {
        val builder = AlertDialog.Builder(context)
        builder.setCancelable(true)
        builder.setOnCancelListener {
            Toast.makeText(context, data.message, Toast.LENGTH_LONG).show()
            delegate.dispatch(JSExecutable(index, JSCallbackType.SUCCESS, "Alert sent successfully"))
        }
        builder.create().show()
    }
}