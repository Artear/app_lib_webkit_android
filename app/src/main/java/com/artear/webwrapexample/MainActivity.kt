package com.artear.webwrapexample

import android.arch.lifecycle.Observer
import android.os.Bundle
import com.artear.tools.error.NestError
import com.artear.ui.base.ArtearActivity
import com.artear.webwrap.WebWrapper
import com.artear.webwrap.presentation.WebCompatViewModel
import com.artear.webwrap.presentation.WebLoadListener
import com.artear.webwrap.repo.WebRepoImpl
import com.artear.webwrap.repo.WebUseCase
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : ArtearActivity() {

    private lateinit var webWrapper: WebWrapper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)


        webWrapper = WebWrapper(webViewExample)
        webWrapper.loadListener = object: WebLoadListener {

            override fun onLoaded() {

            }

        }

        lifecycle.addObserver(webWrapper)

        val viewModel = WebCompatViewModel(webUseCase = WebUseCase(WebRepoImpl()))

        viewModel.data.observe(this, Observer {
            onDataChanged(it)
        })

        viewModel.state.observe(this, Observer {
            uiStateViewModel.state.value = it
        })

        webWrapper.loadUrl("url", viewModel)
    }

    private fun onDataChanged(it: Boolean?) {

    }

    override fun onError(error: NestError) {
        super.onError(error)
        //show error
    }
}