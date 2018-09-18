package com.artear.webwrap

import android.annotation.SuppressLint
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.pm.ApplicationInfo
import android.os.Build
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView


class WebWrapper(private var webView: WebView?) : LifecycleObserver {


    init {
        debugConfig()
        settingsConfig()
        extraConfig()
    }

    private fun debugConfig() {
        webView?.let {
            if (0 != it.context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) {
                WebView.setWebContentsDebuggingEnabled(true)
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun settingsConfig() {
        webView?.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun extraConfig() {
        webView?.setOnTouchListener(null)
        webView?.clearFocus()
        webView?.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
//                Timber.d("WebArticle - WebChromeClient - onProgressChanged - progress: %d", newProgress)
//                if (placeHolder.getVisibility() == VISIBLE && newProgress >= progressMinToHide) {
//                    placeHolder.setVisibility(GONE)
//                    if (needToCreateAnim() && webView != null) {
//                        webView.startAnimation(createAlphaAnimation())
//                    }
//                }
            }
        }
    }

    fun loadUrl(url: String) {

    }

    fun enabledCookieManager() {
        webView?.let {
            CookieManager.getInstance().setAcceptCookie(true)
            if (Build.VERSION.SDK_INT >= 21) {
                it.settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                CookieManager.getInstance().setAcceptThirdPartyCookies(it, true)
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        webView?.apply {
            onResume()
            resumeTimers()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        webView?.apply {
            onPause()
            pauseTimers()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        webView?.apply {
            clearHistory()
            clearCache(true)
            freeMemory()
            destroy()
        }
        webView = null
    }
}