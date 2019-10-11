package com.artear.webwrap

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.webkit.*
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.artear.tools.android.log.logD
import com.artear.tools.android.log.logRes
import com.artear.tools.error.NestErrorFactory
import com.artear.webwrap.presentation.viewside.WebLoadListener
import com.artear.webwrap.presentation.webjs.WebJsEventManager
import com.artear.webwrap.presentation.webnavigation.WebNavigationActionManager
import com.artear.webwrap.util.ActivityWindowConfig

/**
 * A wrapper of your [WebView]. Manage the load of url and is a controller for url override
 * executed in the web page across a [WebNavigationActionManager]. Also has javascript enabled
 * and check all event from them using [WebJsEventManager].
 * The wrapper is a [LifecycleObserver] and is lifecycle-aware.
 *
 * @author David Tolchinsky
 * @param [webView] The [WebView] which will be wrapped
 */
//TODO check memory webview not null
class WebWrapper(internal var webView: WebView?) : LifecycleObserver {

    companion object {

        /**
         * Default value of [progressMinToHide]
         */
        private const val PROGRESS_MIN_TO_HIDE_DEFAULT = 100
        private val matchParentLayout = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
    }

    /**
     * Value of percent progress of the url that is loading to dispatch [WebLoadListener.onLoaded].
     *
     * @see PROGRESS_MIN_TO_HIDE_DEFAULT
     */
    var progressMinToHide = PROGRESS_MIN_TO_HIDE_DEFAULT

    /**
     * The main listener to control the result of the url execution.
     */
    var loadListener: WebLoadListener? = null

    /**
     * The navigation manager associated to this web wrapper.
     */
    var webNavigationActionManager: WebNavigationActionManager? = null

    /**
     * The javascript event manager associated to this web wrapper.
     */
    var webJsEventManager: WebJsEventManager? = null

    /**
     * Show the current progress of the url that is loading this wrapper.
     */
    private var currentProgress: Int = 0

    /**
     * Flag to dispatch [WebLoadListener.onLoading]. Will set false when start the [loadUrl] and
     * true when the event was dispatched.
     */
    private var loadingAction: Boolean = false

    /**
     *  To execute a full screen view. Some use case need the entire screen, like a video.
     *  Can be set by [WebChromeClient.onShowCustomView].
     */
    private var customFullScreenView: View? = null

    /**
     * Used like delegate when need to dispatch [WebChromeClient.CustomViewCallback.onCustomViewHidden]
     */
    private var customViewCallback: WebChromeClient.CustomViewCallback? = null
    private var originalConfig: ActivityWindowConfig? = null

    init {
        debugConfig()
        settingsConfig()
        extraConfig()
    }

    /**
     * Only for debug configuration
     */
    private fun debugConfig() {
        webView?.let {
            if (0 != it.context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    WebView.setWebContentsDebuggingEnabled(true)
                }
            }
        }
    }

    /**
     * Settings configuration of your [WebView]
     */
    @SuppressLint("SetJavaScriptEnabled")
    private fun settingsConfig() {
        webView?.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.allowFileAccessFromFileURLs = true
        }
    }

    /**
     * Any other configuration
     */
    @SuppressLint("ClickableViewAccessibility")
    fun extraConfig() {
        webView?.apply {
            setOnTouchListener(null)
            clearFocus()
            webChromeClient = object : WebChromeClient() {

                override fun onProgressChanged(view: WebView, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    logRes(newProgress) { R.string.progress_load }

                    if (newProgress > 0 && !loadingAction) {
                        loadingAction = true
                        loadListener?.onLoading()
                    }

                    if (newProgress >= progressMinToHide && newProgress != currentProgress) {
                        currentProgress = newProgress
                        loadListener?.onLoaded()
                    }
                }

                override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                    super.onShowCustomView(view, callback)

                    if (customFullScreenView != null) {
                        hideCustomFullScreenView()
                        return
                    }

                    customFullScreenView = view
                    val activity: AppCompatActivity? = context as? AppCompatActivity
                    activity?.apply {
                        val decorView = window.decorView as? FrameLayout
                        decorView?.let {
                            originalConfig = ActivityWindowConfig(it.systemUiVisibility,
                                    requestedOrientation)
                            customViewCallback = callback
                            it.addView(customFullScreenView, matchParentLayout)
                            changeSystemUiVisibility(it)
                            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        }
                    }

                }

                private fun changeSystemUiVisibility(decorView: View) {
                    var uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                            View.SYSTEM_UI_FLAG_FULLSCREEN
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        uiOptions = uiOptions xor View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    }
                    decorView.systemUiVisibility = uiOptions
                }

                override fun onHideCustomView() {
                    super.onHideCustomView()
                    hideCustomFullScreenView()
                }

            }
        }
    }

    /**
     * Call to dismiss the custom view in full screen. Maybe called from [WebChromeClient] or
     * when need to destroy and recover the original orientation.
     */
    private fun hideCustomFullScreenView() {
        logD { "WebWrap - WebView - hideCustomFullScreenView" }
        val activity: AppCompatActivity? = webView?.context as? AppCompatActivity
        activity?.apply {
            originalConfig?.let { config ->
                val decorView = window.decorView as? FrameLayout
                decorView?.let {
                    logD { "WebWrap - WebView - hideCustomFullScreenView - request orientation" }
                    it.removeView(customFullScreenView)
                    customFullScreenView = null
                    it.systemUiVisibility = config.systemUiVisibility
                    requestedOrientation = config.requestedOrientation
                    originalConfig = null
                    // onCustomViewHidden also call onHideCustomView, when come from onDestroy
                    // method but originalConfig was set null and validate that. Anyway not affect
                    // but check for prevent any conflict.
                    customViewCallback?.onCustomViewHidden()
                    customViewCallback = null
                }
            }
        }
    }

    /**
     * Load the javascript interface between your application and [WebView]. That interface
     * is a [WebJsEventManager].
     */
    fun loadJsInterface(webJsEventManager: WebJsEventManager) {
        check(webJsEventManager.commands.isNotEmpty()) {
            "WebWrap - Commands is empty. The WebJsEventManager must have a command as least"
        }
        webView?.let {
            this.webJsEventManager = webJsEventManager
        }
    }

    /**
     * Load a new [urlTarget] into your [WebView].
     *
     * Note that you should load and config your [WebWrapper] previously.
     *
     * @see WebJsEventManager
     * @see WebNavigationActionManager
     */
    fun loadUrl(urlTarget: String) {
        webView?.apply {
            currentProgress = 0
            loadingAction = false
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
                    logRes(url) { R.string.override_url }
                    return overrideUrlLoading(view, Uri.parse(url))
                }

                @TargetApi(Build.VERSION_CODES.N)
                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                    logRes(request.url.toString()) { R.string.override_url_nougat }
                    return overrideUrlLoading(view, request.url)
                }
            }

            setBackgroundColor(Color.TRANSPARENT)
            loadUrl(urlTarget)
        }
    }

    /**
     * Unify the override url android webkit api in only one function that receive a [Uri].
     */
    private fun overrideUrlLoading(view: WebView, uri: Uri): Boolean {
        webNavigationActionManager?.run {
            return processUri(view.context, uri)
        }
        return false
    }

    /**
     * Call this method for enable the cookies in your [WebView].
     */
    fun enabledCookieManager() {
        webView?.let {
            CookieManager.getInstance().setAcceptCookie(true)
            if (Build.VERSION.SDK_INT >= 21) {
                it.settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                CookieManager.getInstance().setAcceptThirdPartyCookies(it, true)
            }
        }
    }

    /**
     * Part of [LifecycleObserver] flow. Execute at resume the lifecycle an notify to the [WebView].
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        logD { "WebWrap - onResume - webView = ${webView?.id}" }
        webView?.apply {
            onResume()
            resumeTimers()
        }
    }

    /**
     * Part of [LifecycleObserver] flow. Execute at pause the lifecycle an notify to the [WebView].
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        logD { "WebWrap - onPause - webView = ${webView?.id}" }
        webView?.apply {
            onPause()
            pauseTimers()
        }
    }

    /**
     * Part of [LifecycleObserver] flow. Clean all objects. Also the cache on the [WebView].
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        logD { "WebWrap - onDestroy - webView = ${webView?.id}" }
        webView?.apply {
            hideCustomFullScreenView()
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