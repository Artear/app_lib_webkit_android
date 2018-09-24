package com.artear.webwrap.repo

import com.artear.webwrap.log
import java.net.HttpURLConnection
import java.net.URL


class WebRepoImpl : WebRepository {

    override fun canReachUrl(url: String, timeout: Int): Boolean {
        return try {
            canReachBaseURL(url, timeout)
        } catch (e: Exception) {
            log(url, e.message!!) { "Fail trying to reach web url = %s. Message = %s" }
            false
        }
    }

    @Throws(Exception::class)
    private fun canReachBaseURL(urlBase: String, timeout: Int): Boolean{

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
        return validResponseCode(con.responseCode)
    }

    private fun validResponseCode(responseCode: Int): Boolean {
        return responseCode == 200 || responseCode == 302 ||
                responseCode == 301 || responseCode == 304
    }
}