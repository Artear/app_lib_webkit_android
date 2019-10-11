package com.artear.webwrap.presentation.webjs.event

import com.artear.webwrap.presentation.webjs.EventJsData
import com.squareup.moshi.JsonClass

/**
 * Javascript alert model
 */
@JsonClass(generateAdapter = true)
data class AlertJsData(val title: String, val message: String) : EventJsData
