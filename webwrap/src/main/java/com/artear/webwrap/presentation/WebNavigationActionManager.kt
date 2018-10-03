package com.artear.webwrap.presentation

import android.content.Context
import android.net.Uri


class WebNavigationActionManager {

    private val actions = arrayListOf<WebNavigationAction>()

    fun addAction(webNavigationAction: WebNavigationAction) {
        actions.add(webNavigationAction)
    }

    fun removeAllActions() {
        actions.clear()
    }

    fun processUri(context: Context, uri: Uri): Boolean {
        var cached = false
        actions.forEach { action ->
            if (action.canExecute(uri)) {
                cached = true
                action.execute(context, uri)
            }
        }
        return cached
    }
}