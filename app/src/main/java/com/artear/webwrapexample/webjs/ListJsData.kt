package com.artear.webwrapexample.webjs

import com.artear.webwrap.presentation.webjs.EventJsData

//@JsonClass(generateAdapter = true)
//TODO try injector to use this
class ListJsData<T>(val items : List<T>) : EventJsData