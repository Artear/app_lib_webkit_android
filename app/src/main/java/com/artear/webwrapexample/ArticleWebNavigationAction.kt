package com.artear.webwrapexample

import android.content.Context
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import com.artear.webwrap.log
import com.artear.webwrap.presentation.WebNavigationAction


class ArticleWebNavigationAction : WebNavigationAction {

    override fun execute(context: Context, uri: Uri): Boolean {
        if(context is AppCompatActivity){

        }
        log { "ArticleWebNavigationAction - The context $context is not an AppCompatActivity" }
        return false
    }

}