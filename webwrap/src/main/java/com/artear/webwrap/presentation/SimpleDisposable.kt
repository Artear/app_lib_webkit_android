package com.artear.webwrap.presentation

import com.artear.tools.error.NestError
import com.artear.tools.error.NestErrorFactory
import io.reactivex.observers.DisposableObserver


class SimpleDisposable<T>(var onNextDelegate: (data: T) -> Unit,
                          var onErrorDelegate: (e: NestError) -> Unit) : DisposableObserver<T>() {

    override fun onComplete() {}

    override fun onNext(data: T) {
        onNextDelegate(data)
    }

    override fun onError(e: Throwable) {
        val error = NestErrorFactory.create(e)
        onErrorDelegate(error)
    }

}
