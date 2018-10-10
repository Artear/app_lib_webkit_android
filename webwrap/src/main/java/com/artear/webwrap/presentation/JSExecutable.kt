package com.artear.webwrap.presentation

data class JSExecutable(val index: Int, val type: JSCallbackType, val data: String? = "true")

enum class JSCallbackType {
    SUCCESS, ERROR;

    override fun toString(): String {
        return super.toString().toLowerCase()
    }
}