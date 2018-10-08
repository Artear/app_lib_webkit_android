package com.artear.webwrap

import android.webkit.JavascriptInterface
import com.artear.annotations.TestAnnotation

@TestAnnotation(key = "log")
open class Log {

    fun execute(index: Int, message: String) {
        //start activity
    }

}

@TestAnnotation(key = "alert")
class Alert {

    fun execute(index: Int, title: String, message: String) {

    }
}

@JavascriptInterface
fun Log.log(index: Int, message: String) {
    execute(index, message)
}