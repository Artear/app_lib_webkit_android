package com.artear.webwrap.presentation.webjs.event

import com.artear.webwrap.presentation.webjs.EventJsData
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LogJsData(val message: String) : EventJsData
