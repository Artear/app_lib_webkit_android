package com.artear.webwrapexample.webjs

import com.artear.webwrap.presentation.webjs.EventJsData
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ImagesJsData(val images: List<ImagesJsData>) : EventJsData

@JsonClass(generateAdapter = true)
class ImageJsData(val text: String, val src: String) : EventJsData