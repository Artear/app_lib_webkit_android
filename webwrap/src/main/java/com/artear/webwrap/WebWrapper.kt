package com.artear.webwrap

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.webkit.WebView


class WebWrapper(private var webView: WebView?) : LifecycleObserver {




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