package com.artear.webwrap

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.pm.ApplicationInfo
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.webkit.*
import com.artear.webwrap.presentation.viewside.WebLoadListener
import com.artear.webwrap.presentation.webjs.WebJsEventManager
import com.artear.webwrap.presentation.webnavigation.WebNavigationActionManager

//TODO check memory webview not null
class WebWrapper(private var webView: WebView?) : LifecycleObserver {

    var progressMinToHide = PROGRESS_MIN_TO_HIDE_DEFAULT
    var loadListener: WebLoadListener? = null
    var webNavigationActionManager: WebNavigationActionManager? = null
    var webJsEventManager: WebJsEventManager? = null

    companion object {
        private const val PROGRESS_MIN_TO_HIDE_DEFAULT = 100
    }

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
    fun extraConfig() {
        webView?.apply {
            setOnTouchListener(null)
            clearFocus()
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    log(newProgress) { R.string.progress_load }
                    if (newProgress >= progressMinToHide) loadListener?.onLoaded()
                }
            }
        }
    }

    fun loadJsInterface(webJsEventManager: WebJsEventManager, autoInit: Boolean = true){
        webView?.let {
            if(autoInit) webJsEventManager.initialize(it)
            this.webJsEventManager = webJsEventManager
        }
    }

    fun loadUrl(urlTarget: String) {
        webView?.apply {
            webViewClient = object : WebViewClient() {
                override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                    super.onReceivedError(view, request, error)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (request.url.toString() == urlTarget) {
                            loadListener?.onError()
                        }
                    }
                }

                override fun onReceivedHttpError(view: WebView, request: WebResourceRequest,
                                                 errorResponse: WebResourceResponse) {
                    super.onReceivedHttpError(view, request, errorResponse)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (request.url.toString() == urlTarget) {
                            loadListener?.onError()
                        }
                    }
                }

                @Suppress("OverridingDeprecatedMember")
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    log(url) { R.string.override_url }
                    return overrideUrlLoading(view, Uri.parse(url))
                }

                @TargetApi(Build.VERSION_CODES.N)
                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                    log(request.url.toString()) { R.string.override_url_nougat }
                    return overrideUrlLoading(view, request.url)
                }
            }

            setBackgroundColor(Color.TRANSPARENT)
            loadUrl(urlTarget)
        }
    }

    private fun overrideUrlLoading(view: WebView, uri: Uri): Boolean {
        webNavigationActionManager?.run {
            return processUri(view.context, uri)
        }
        return false
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
        log { "WebWrap - onResume - webView = ${webView?.id}" }
        webView?.apply {
            onResume()
            resumeTimers()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        log { "WebWrap - onPause - webView = ${webView?.id}" }
        webView?.apply {
            onPause()
            pauseTimers()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        log { "WebWrap - onDestroy - webView = ${webView?.id}" }
        webView?.apply {
            webJsEventManager?.removeJavascriptInterfaces(this)
            clearHistory()
            clearCache(true)
            @Suppress("DEPRECATION")
            freeMemory()
            destroy()
        }
        webNavigationActionManager?.removeAllActions()
        webNavigationActionManager = null
        webJsEventManager?.removeAllCommands()
        webJsEventManager = null
        loadListener = null
        webView = null
    }
}