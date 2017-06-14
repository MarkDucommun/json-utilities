package com.hcsc.de.claims.results.observableResults

import com.hcsc.de.claims.results.Failure
import com.hcsc.de.claims.results.Success
import org.junit.Assert.fail

infix fun <failureType, successType> SingleResult<failureType, successType>.succeedsAnd(onSuccess: (successType) -> Unit) {
    blockingGet().let { result ->
        when (result) {
            is Success -> onSuccess(result.content)
            is Failure -> fail("Result should have been a Success")
        }
    }
}

infix fun <failureType, successType> SingleResult<failureType, successType>.failsAnd(onFailure: (failureType) -> Unit) {
    blockingGet().let { result ->
        when (result) {
            is Success -> fail("Result should have been a Failure")
            is Failure -> onFailure(result.content)
        }
    }
}