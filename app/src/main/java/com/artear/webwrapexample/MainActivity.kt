package com.artear.webwrapexample

import android.os.Bundle
import androidx.lifecycle.Observer
import com.artear.tools.android.log.logD
import com.artear.tools.error.NestError
import com.artear.ui.base.ArtearActivity
import com.artear.webwrap.WebWrapper
import com.artear.webwrap.loadAllJsInterface
import com.artear.webwrap.presentation.viewside.WebLoadListener
import com.artear.webwrap.presentation.vm.WebCompatViewModel
import com.artear.webwrap.presentation.webjs.WebJsDispatcher
import com.artear.webwrap.presentation.webjs.WebJsEventManager
import com.artear.webwrap.presentation.webnavigation.WebNavigationActionManager
import com.artear.webwrap.repo.WebRepoImpl
import com.artear.webwrap.repo.WebUseCase
import com.artear.webwrapexample.webjs.*
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : ArtearActivity() {

    private lateinit var webWrapper: WebWrapper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        val url = "file:///android_asset/index.html"

        webWrapper = WebWrapper(webViewExample)
        webWrapper.loadListener = object: WebLoadListener {
            override fun onLoading() {
                logD { "WebLoadListener - onLoading!" }
            }

            override fun onError(error : NestError) {
                logD { "WebLoadListener - onError - errorType = ${error.type}"}
            }

            override fun onLoaded() {
                logD { "WebLoadListener - OnLoaded!" }
            }
        }

        //Load Navigation controller
        val navigationActionManager = WebNavigationActionManager()
        navigationActionManager.addAction(ArticleWebNavigationAction())
        webWrapper.webNavigationActionManager = navigationActionManager

        //Load Event Js Controller
        val webJsEventManager = WebJsEventManager()

        val delegate : WebJsDispatcher = { webJsEventManager.executeJS(it) }
        webJsEventManager.commands.add(ImagesJs(this, Images(), delegate ))
        webJsEventManager.commands.add(MetadataJs(this, Metadata(), delegate ))
        webJsEventManager.commands.add(PlaylistJs(this, Playlist(), delegate ))

        webWrapper.loadAllJsInterface(webJsEventManager)

        lifecycle.addObserver(webWrapper)

        loadUrl(url)
    }

    private fun loadUrl(url: String) {
        val viewModel = WebCompatViewModel(webUseCase = WebUseCase(WebRepoImpl()))

        viewModel.data.observe(this, Observer {
            logD { "Data changed! - now load url in web view!" }
            webWrapper.loadUrl(url)
        })

        viewModel.state.observe(this, Observer {
            uiStateViewModel.state.value = it
        })

        viewModel.requestData(url)
    }

    private fun onDataChanged(it: Boolean?) {

    }

     override fun onError(error: NestError) {
         super.onError(error)
        //show error
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(webWrapper)
    }
}