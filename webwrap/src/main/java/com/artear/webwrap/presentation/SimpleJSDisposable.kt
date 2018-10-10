package com.artear.webwrap.presentation

import com.artear.webwrap.JSExecuteException
import io.reactivex.observers.DisposableObserver


class SimpleJSDisposable(var onDelegate: (data: JSExecutable) -> Unit) : DisposableObserver<JSExecutable>() {

    override fun onComplete() {}

    override fun onNext(data: JSExecutable) {
        onDelegate(data)
    }

    override fun onError(e: Throwable) {
        if (e is JSExecuteException) {
            onDelegate(JSExecutable(e.index, JSCallbackType.ERROR, e.message))
        }
    }

}
