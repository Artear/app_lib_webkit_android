package com.artear.webwrap.presentation.webnavigation

import android.content.Context
import android.net.Uri
import com.artear.webwrap.WebWrapper
import com.artear.webwrap.presentation.webjs.WebJsEventManager

/**
 * A manager that have some actions to process. This actions should be added before load the url in
 * [WebWrapper]. The wrapper has a client to override the loading of any url in the web view and
 * use [WebWrapper.overrideUrlLoading] to pass the responsibility to this class to process
 * that [Uri].
 *
 * The actions must be a [WebNavigationAction] and your aim is catch only the url that need and
 * the result is a navigation inside or outside your application.
 *
 * Any another action that does not imply a navigation must be implements with [WebJsEventManager].
 *
 * @see WebJsEventManager
 * @see WebWrapper
 */
class WebNavigationActionManager {

    /**
     * The list of [WebNavigationAction]
     */
    private val actions = arrayListOf<WebNavigationAction>()

    /**
     * Add a new action to this manager.
     */
    fun addAction(webNavigationAction: WebNavigationAction) {
        actions.add(webNavigationAction)
    }

    /**
     * Remove all actions from manager.
     */
    fun removeAllActions() {
        actions.clear()
    }

    /**
     * Called from [WebWrapper] to process each [Uri] dispatched by the web view client.
     */
    fun processUri(context: Context, uri: Uri): Boolean {
        return actions.any {
            it.execute(context, uri)
        }
    }
}