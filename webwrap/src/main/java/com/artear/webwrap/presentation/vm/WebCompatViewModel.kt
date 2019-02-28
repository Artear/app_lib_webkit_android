package com.artear.webwrap.presentation.vm

import com.artear.domain.coroutine.SimpleReceiver
import com.artear.tools.android.isMarshMallow
import com.artear.ui.model.State
import com.artear.ui.viewmodel.ArtearViewModel
import com.artear.webwrap.repo.WebUseCase

/**
 *
 */
class WebCompatViewModel(private val webUseCase: WebUseCase) : ArtearViewModel<Boolean>() {


    override fun requestBaseData(vararg params: Any) {
        isMarshMallow({
            val url = params[0] as String
            executeCompatLoadUrl(url)
        }, {
            data.value = true
            state.value = State.Success
        })
    }

    private fun executeCompatLoadUrl(url: String) {
        webUseCase(url, SimpleReceiver(
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
        webUseCase.dispose()
    }
}