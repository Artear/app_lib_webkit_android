package com.artear.webwrap.presentation.webjs.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LogJSData(val message: String)
