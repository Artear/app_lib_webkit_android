package com.artear.webwrap.presentation.webjs

import android.annotation.SuppressLint
import com.artear.webwrap.JSExecuteException
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers


@SuppressLint("CheckResult")
fun <T> result(observable: Observable<T>) : Observable<T>{
    return observable.subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
}

@SuppressLint("CheckResult")
fun error(index: Int, message: String?, observer: JsDisposableObserver) {
    Observable.error<JSExecutable>(JSExecuteException(index, message)).subscribeWith(observer)
}