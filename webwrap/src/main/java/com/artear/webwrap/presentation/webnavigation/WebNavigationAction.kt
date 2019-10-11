package com.artear.webwrap.presentation.webnavigation

import android.content.Context
import android.net.Uri

/**
 * An action that imply a possible navigation inside or outside your application.
 * Maybe can be a filter to respond to a particular url.
 *
 * @see WebNavigationActionManager
 */
interface WebNavigationAction {

    /**
     * Run a validation and process the [Uri].
     *
     * @return True, if you can respond to the [Uri], false otherwise.
     */
    fun execute(context: Context, uri: Uri) : Boolean
}