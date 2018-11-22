package com.artear.webwrapexample

import android.content.Context
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import com.artear.webwrap.presentation.webnavigation.WebNavigationAction
import com.artear.webwrap.util.log


class ArticleWebNavigationAction : WebNavigationAction {

    override fun execute(context: Context, uri: Uri): Boolean {
        if(context is AppCompatActivity){
            log { "ArticleWebNavigationAction - The uri is $uri" }
            return true
        }
        log { "ArticleWebNavigationAction - The context $context is not an AppCompatActivity" }
        return false
    }

}