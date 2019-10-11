package com.artear.webwrap.presentation.webjs.event

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.artear.injector.api.JsInterface
import com.artear.webwrap.presentation.webjs.*


/**
 * Util defer event to show an alert.
 *
 * @see WebJsEventManager
 */
@JsInterface("alert")
class Alert : DeferEventJs<AlertJsData> {

    override fun event(context: Context, delegate: WebJsDispatcher, index: Int, data: AlertJsData) {
        val builder = AlertDialog.Builder(context)
        builder.setCancelable(true)
        builder.setOnCancelListener {
            Toast.makeText(context, data.message, Toast.LENGTH_LONG).show()
            delegate.dispatch(JSExecutable(index, JSCallbackType.SUCCESS, "Alert sent successfully"))
        }
        builder.setTitle(data.title)
        builder.setMessage(data.message)
        builder.create().show()
    }
}