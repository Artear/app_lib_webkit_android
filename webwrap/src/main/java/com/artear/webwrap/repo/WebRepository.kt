package com.artear.webwrap.repo

import android.webkit.WebView

/**
 * Simple interface to generate the entity of a web repository that knows if can execute a url.
 */
interface WebRepository {

    /**
     * Go to any place to valid if the device and the [WebView] can execute the [url].
     *
     * @param timeout is needed for a good user experience.
     */
    @Throws(Exception::class)
    fun canReachUrl(url: String, timeout: Int): Boolean
}
