package com.artear.webwrap.repo

import com.artear.tools.android.log.logD
import java.net.HttpURLConnection
import java.net.URL


/**
 * The implementation of [WebRepository].
 *
 * Check with a simple network call using [HttpURLConnection].
 */
class WebRepoImpl : WebRepository {

    /**
     * Call to [canReachBaseURL] and return the result value. If any go wrong log the error and
     * return false.
     */
    override fun canReachUrl(url: String, timeout: Int): Boolean {
        return try {
            canReachBaseURL(url, timeout)
        } catch (e: Exception) {
            logD { "Fail trying to reach web url = $url. Message = ${e.message!!}" }
            false
        }
    }

    /**
     * Using a [URL] and [URL.openConnection] make a request to check if return a [validResponseCode].
     */
    @Throws(Exception::class)
    private fun canReachBaseURL(urlBase: String, timeout: Int): Boolean {

        // Required due to android bug: http://code.google.com/p/android/issues/detail?id=7786
        // Should happen only on Froyo and below, but is still present here.
        System.setProperty("http.keepAlive", "false")
        val url = URL(urlBase)
        HttpURLConnection.setFollowRedirects(false)
        // note : you may also need => HttpURLConnection.setInstanceFollowRedirects(false);
        val con = url.openConnection() as HttpURLConnection
        con.connectTimeout = timeout
        con.readTimeout = timeout
        con.requestMethod = "GET"

        check(validResponseCode(con.responseCode)) {
            "Can not load url, the response code = ${con.responseCode} not is valid"
        }

        return true
    }

    /**
     * Validate the [responseCode] with some little codes.
     */
    private fun validResponseCode(responseCode: Int): Boolean {
        return responseCode == 200 || responseCode == 302 ||
                responseCode == 301 || responseCode == 304
    }
}