package com.hcsc.de.claims.results.observableResults

import com.hcsc.de.claims.results.Failure
import com.hcsc.de.claims.results.Success
import io.reactivex.Single

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
