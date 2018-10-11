package com.artear.webwrap.presentation.webjs

import com.artear.webwrap.JSExecuteException
import io.reactivex.observers.DisposableObserver


class JsDisposableObserver(var onDelegate: (data: JSExecutable) -> Unit) : DisposableObserver<JSExecutable>() {

    override fun onComplete() {
        dispose()
    }

    override fun onNext(data: JSExecutable) {
        onDelegate(data)
    }

    override fun onError(e: Throwable) {
        if (e is JSExecuteException) {
            onDelegate(JSExecutable(e.index, JSCallbackType.ERROR, e.message))
        }
    }

}
