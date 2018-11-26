package com.artear.webwrap

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.*
import android.widget.FrameLayout
import com.artear.tools.error.NestErrorFactory
import com.artear.webwrap.presentation.viewside.WebLoadListener
import com.artear.webwrap.presentation.webjs.WebJsEventManager
import com.artear.webwrap.presentation.webnavigation.WebNavigationActionManager
import com.artear.webwrap.util.ActivityWindowConfig
import com.artear.webwrap.util.log


//TODO check memory webview not null
class WebWrapper(internal var webView: WebView?) : LifecycleObserver {

    companion object {
        private const val PROGRESS_MIN_TO_HIDE_DEFAULT = 100
    }

    var progressMinToHide = PROGRESS_MIN_TO_HIDE_DEFAULT
    var loadListener: WebLoadListener? = null
    var webNavigationActionManager: WebNavigationActionManager? = null
    var webJsEventManager: WebJsEventManager? = null
    private var currentProgress: Int = 0

    init {
        debugConfig()
        settingsConfig()
        extraConfig()
    }

    private fun debugConfig() {
        webView?.let {
            if (0 != it.context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    WebView.setWebContentsDebuggingEnabled(true)
                }
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun settingsConfig() {
        webView?.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.allowFileAccessFromFileURLs = true
        }
    }

    val matchParentLayout = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)

    private var customView: View? = null
    private var customViewCallback: WebChromeClient.CustomViewCallback? = null
    private var originalConfig: ActivityWindowConfig? = null

    @SuppressLint("ClickableViewAccessibility")
    fun extraConfig() {
        webView?.apply {
            setOnTouchListener(null)
            clearFocus()
            webChromeClient = object : WebChromeClient() {

                override fun onProgressChanged(view: WebView, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    log(newProgress) { R.string.progress_load }
                    if (newProgress >= progressMinToHide && newProgress != currentProgress) {
                        currentProgress = newProgress
                        loadListener?.onLoaded()
                    }
                }

                override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                    super.onShowCustomView(view, callback)

                    if (customView != null) {
                        onHideCustomView()
                        return
                    }

                    customView = view
                    val activity: AppCompatActivity? = context as? AppCompatActivity
                    activity?.apply {
                        val decorView = window.decorView
                        originalConfig = ActivityWindowConfig(decorView.systemUiVisibility,
                                requestedOrientation)
                        customViewCallback = callback
                        (decorView as FrameLayout).addView(customView, matchParentLayout)
//                        toggledFullscreen(false)
                        var uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            uiOptions = uiOptions xor View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        }
                        decorView.setSystemUiVisibility(uiOptions)
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//                        decorView.setSystemUiVisibility(3846)
                    }



                }

                override fun onHideCustomView() {
                    super.onHideCustomView()
                    val activity: AppCompatActivity? = context as? AppCompatActivity
                    activity?.apply {
                        originalConfig?.let {
                            val decorView = window.decorView
                            (decorView as FrameLayout).removeView(customView)
                            customView = null
//                            toggledFullscreen(false)
                            decorView.systemUiVisibility = it.systemUiVisibility
                            requestedOrientation = it.requestedOrientation
                            customViewCallback?.onCustomViewHidden()
                            customViewCallback = null
                        }
                    }
                }

                fun toggledFullscreen(fullscreen :Boolean) {
                    // Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
                    val activity: AppCompatActivity? = context as? AppCompatActivity
                    activity?.let {
                        if (fullscreen) {
                            val attrs = activity.window.attributes
                            attrs.flags = attrs.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
                            attrs.flags = attrs.flags or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                            activity.window.attributes = attrs
                            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE
                        } else {
                            val attrs = activity.window.attributes
                            attrs.flags = attrs.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
                            attrs.flags = attrs.flags and WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON.inv()
                            activity.window.attributes = attrs
                            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                        }
                    }
                }
            }
        }
    }

    fun loadJsInterface(webJsEventManager: WebJsEventManager) {
        check(webJsEventManager.commands.isNotEmpty()) {
            "WebWrap - Commands is empty. The WebJsEventManager must have a command as least"
        }
        webView?.let {
            this.webJsEventManager = webJsEventManager
        }
    }

    fun loadUrl(urlTarget: String) {
        webView?.apply {
            currentProgress = 0

            //TODO check webViewClient
            // urlTarget and loadListener both final, see old references maybe create a class
            // and pass the listener. set new url target each time to load for validation
            webViewClient = object : WebViewClient() {
                override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                    super.onReceivedError(view, request, error)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (request.url.toString() == urlTarget) {
                            loadListener?.onError(NestErrorFactory.create(error))
                        }
                    }
                }

                override fun onReceivedHttpError(view: WebView, request: WebResourceRequest,
                                                 errorResponse: WebResourceResponse) {
                    super.onReceivedHttpError(view, request, errorResponse)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (request.url.toString() == urlTarget) {
                            loadListener?.onError(NestErrorFactory.create(errorResponse))
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