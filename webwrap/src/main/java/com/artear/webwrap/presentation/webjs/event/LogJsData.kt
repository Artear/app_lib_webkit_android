package com.artear.webwrap.presentation.webjs.event

import com.artear.webwrap.presentation.webjs.EventJsData
import com.squareup.moshi.JsonClass


/**
 * Javascript log model.
 */
@JsonClass(generateAdapter = true)
data class LogJsData(val message: String) : EventJsData
