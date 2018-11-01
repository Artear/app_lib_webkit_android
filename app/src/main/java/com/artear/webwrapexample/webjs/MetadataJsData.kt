package com.artear.webwrapexample.webjs

import com.artear.webwrap.presentation.webjs.EventJsData
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class MetadataJsData(val title: String, val share: ShareJsData) : EventJsData

@JsonClass(generateAdapter = true)
class ShareJsData(val url: String) : EventJsData
