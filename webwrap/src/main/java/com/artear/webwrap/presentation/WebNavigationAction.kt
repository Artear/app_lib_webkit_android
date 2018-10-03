package com.artear.webwrap.presentation

import android.content.Context
import android.net.Uri


interface WebNavigationAction {

    fun execute(context: Context, uri: Uri)
    fun canExecute(uri: Uri): Boolean
}