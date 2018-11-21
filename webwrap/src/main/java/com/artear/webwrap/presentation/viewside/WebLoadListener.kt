package com.artear.webwrap.presentation.viewside

import com.artear.tools.error.NestError

interface WebLoadListener {

    fun onLoaded()
    fun onError(error: NestError)
}
