package com.artear.webwrap.repo

import com.artear.domain.coroutine.UseCase
import com.artear.webwrap.WebWrapper

/**
 * Main use case used in [WebWrapper]. The goal is ensure the possibility to call this url.
 *
 */
class WebUseCase(private val webRepository: WebRepository) : UseCase<String, Boolean>() {

    companion object {
        /**
         * Default timeout for check if can reach the url.
         */
        private const val DEFAULT_TIMEOUT = 5000
    }

    /**
     * Execute with [DEFAULT_TIMEOUT] a call to this [param] (the url) and return true if a
     * success call.
     */
    override suspend fun execute(param: String?): Boolean {
        return webRepository.canReachUrl(param!!, DEFAULT_TIMEOUT)
    }
}