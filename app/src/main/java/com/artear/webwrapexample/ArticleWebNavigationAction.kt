package com.artear.webwrapexample

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import com.artear.tools.android.log.logD
import com.artear.webwrap.presentation.webnavigation.WebNavigationAction

/**
 * Example of an [WebNavigationAction] that can catch and react to this [Uri].
 */
class ArticleWebNavigationAction : WebNavigationAction {

    override fun execute(context: Context, uri: Uri): Boolean {
        if(context is AppCompatActivity){
            logD { "ArticleWebNavigationAction - The uri is $uri" }
            return true
        }
        logD { "ArticleWebNavigationAction - The context $context is not an AppCompatActivity" }
        return false
    }

}