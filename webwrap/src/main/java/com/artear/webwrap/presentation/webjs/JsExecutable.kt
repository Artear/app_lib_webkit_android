package com.artear.webwrap.presentation.webjs

/**
 * Simple javascript executable that is needed to communicate with the web page.
 *
 * @see WebJsEventManager
 */
data class JSExecutable(val index: Int, val type: JSCallbackType, val data: String? = "true")

/**
 * Two types for inform to web page the result of execution.
 */
enum class JSCallbackType {
    SUCCESS, ERROR;

    override fun toString(): String {
        return super.toString().toLowerCase()
    }
}