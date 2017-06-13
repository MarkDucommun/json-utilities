package com.hcsc.de.claims.results.observableResults

import com.hcsc.de.claims.results.Failure
import com.hcsc.de.claims.results.Result
import com.hcsc.de.claims.results.Success

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
