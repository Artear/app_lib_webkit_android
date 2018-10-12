package com.artear.webwrap.presentation.webnavigation

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
        return actions.any {
            it.execute(context, uri)
        }
    }
}