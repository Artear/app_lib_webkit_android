package com.artear.webwrap.presentation

import com.artear.ui.model.State
import com.artear.ui.viewmodel.ArtearViewModel
import com.artear.webwrap.repo.WebUseCase


class WebCompatViewModel(private val useCase: WebUseCase) : ArtearViewModel<Boolean>() {

    override fun requestBaseData(vararg params: Any) {
        val url = params[0] as String
        useCase.getObservable(url, true)
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
}