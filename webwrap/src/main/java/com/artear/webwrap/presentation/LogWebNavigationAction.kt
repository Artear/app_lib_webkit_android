package com.artear.webwrap.presentation

import android.content.Context
import android.net.Uri


class LogWebNavigationAction : WebNavigationAction{

    override fun execute(context: Context, uri: Uri) {

    }

    override fun canExecute(uri: Uri): Boolean = true
}