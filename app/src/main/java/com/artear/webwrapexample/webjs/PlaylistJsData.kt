package com.artear.webwrapexample.webjs

import com.artear.webwrap.presentation.webjs.EventJsData
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class PlaylistJsData(val videos: List<VideoJsData>) : EventJsData

@JsonClass(generateAdapter = true)
class VideoJsData(val text: String, val src: String, val source: String) : EventJsData
