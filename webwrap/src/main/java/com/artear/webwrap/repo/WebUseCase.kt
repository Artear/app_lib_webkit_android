package com.artear.webwrap.repo

import com.artear.domain.UseCase
import io.reactivex.Observable


class WebUseCase(private val webRepository : WebRepository) : UseCase<String, Boolean>() {

    companion object {
        private const val DEFAULT_TIMEOUT = 5000
    }

    override fun getObservableFactory(param: String?): Observable<Boolean> {

        return try {
            Observable.just(webRepository.canReachUrl(param!!, DEFAULT_TIMEOUT))
        } catch (e: Exception) {
            Observable.error<Boolean>(e)
        }
    }
}