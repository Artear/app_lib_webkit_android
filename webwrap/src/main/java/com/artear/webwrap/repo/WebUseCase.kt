package com.artear.webwrap.repo

import com.artear.domain.coroutine.UseCase


class WebUseCase(private val webRepository : WebRepository) : UseCase<String, Boolean>() {

    companion object {
        private const val DEFAULT_TIMEOUT = 5000
    }

    override suspend fun execute(param: String?): Boolean {
        return webRepository.canReachUrl(param!!, DEFAULT_TIMEOUT)
    }
}