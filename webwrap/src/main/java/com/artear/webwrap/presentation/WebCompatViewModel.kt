package com.artear.webwrap.presentation

import android.os.Build
import com.artear.ui.model.State
import com.artear.ui.viewmodel.ArtearViewModel
import com.artear.webwrap.repo.WebUseCase

/**
 *
 */
class WebCompatViewModel(private val webUseCase: WebUseCase) : ArtearViewModel<Boolean>() {

    private lateinit var simpleDisposable: SimpleDisposable<Boolean>

    override fun requestBaseData(vararg params: Any) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            val url = params[0] as String
            executeCompatLoadUrl(url)
        } else {
            data.value = true
            state.value = State.Success
        }
    }

    private fun executeCompatLoadUrl(url: String) {
        simpleDisposable = webUseCase.getObservable(url, true)
                .subscribeWith(SimpleDisposable(
                        onNextDelegate = {
                            data.value = it
                            state.value = State.Success
                        },
                        onErrorDelegate = {
                            state.value = State.Error(it)
                        })
                )
    }

    override fun onCleared() {
        super.onCleared()
        simpleDisposable.dispose()
    }
}