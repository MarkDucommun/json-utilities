package com.hcsc.de.claims

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single

sealed class Result<failureType, successType>

data class Success<failureType, successType>(
        val content: successType
) : Result<failureType, successType>()

data class Failure<failureType, successType>(
        val content: failureType
) : Result<failureType, successType>()

typealias ObservableResult<failureType, successType> = Observable<Result<failureType, successType>>
typealias SingleResult<failureType, successType> = Single<Result<failureType, successType>>

fun <failureType, successType, newSuccessType> SingleResult<failureType, successType>.flatMapSuccess(
        onSuccess: (successType) -> SingleResult<failureType, newSuccessType>
): SingleResult<failureType, newSuccessType> {

    return this.flatMap { result ->

        when (result) {
            is Success -> onSuccess.invoke(result.content)
            is Failure -> Single.just(Failure<failureType, newSuccessType>(result.content))
        }
    }
}

fun <failureType, successType> SingleResult<failureType, successType>.mapSuccess(
        onSuccess: (successType) -> Result<failureType, successType>
): SingleResult<failureType, successType> {

    return this.map { result ->

        when (result) {
            is Success -> onSuccess.invoke(result.content)
            is Failure -> result
        }
    }
}

fun <failureType, successType> List<SingleResult<failureType, successType>>.concat() = Single.concat(this)

val EMPTY_SUCCESS = Success<String, Unit>(Unit)