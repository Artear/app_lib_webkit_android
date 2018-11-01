package com.artear.webwrap.repo

interface WebRepository {

    @Throws(Exception::class)
    fun canReachUrl(url: String, timeout: Int): Boolean
}
