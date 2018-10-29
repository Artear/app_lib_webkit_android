package com.artear.webwrap.presentation.webnavigation

import android.content.Context
import android.net.Uri

/**
 * Works like a filter. Just add all [domains] that you want to manage, and this navigation action
 * will intercept all other urls that not contain in [domains]
 */
class WebNavigationActionPolicyDomain(private val domains: MutableList<String>) : WebNavigationAction {

    override fun execute(context: Context, uri: Uri): Boolean {
        //There are no any domain in this list that is contained for uri host -> return true
        //If there are almost one domain that is contained for uri host -> return false
        return !domains.any { uri.host.contains(it) }
    }
}