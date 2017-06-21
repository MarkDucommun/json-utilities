package com.hcsc.de.claims.results

import org.assertj.core.api.KotlinAssertions.assertThat
import org.junit.Assert.fail

infix fun <failureType, successType> Result<failureType, successType>.succeedsAnd(onSuccess: (successType) -> Unit) {
    when (this) {
        is Success -> onSuccess(this.content)
        is Failure -> fail("Result should have been a Success: $content")
    }
}

infix fun <failureType, successType> Result<failureType, successType>.succeedsAndShouldReturn(expectedObject: successType) {
    when (this) {
        is Success -> assertThat(content).isEqualTo(expectedObject)
        is Failure -> fail("Result should have been a Success: $content")
    }
}

infix fun <failureType, successType> Result<failureType, successType>.failsAnd(onFailure: (failureType) -> Unit) {
    when (this) {
        is Success -> fail("Result should have been a Failure")
        is Failure -> onFailure(this.content)
    }
}

infix fun <successType> Result<String, successType>.failsWithMessage(expectedMessage: String) {
    failsAndShouldReturn(expectedFailure = expectedMessage)
}

infix fun <failureType, successType> Result<failureType, successType>.failsAndShouldReturn(expectedFailure: failureType) {
    when (this) {
        is Success -> fail("Result should have been a Failure")
        is Failure -> assertThat(content).isEqualTo(expectedFailure)
    }
}


val <failureType, successType> Result<failureType, successType>.get: successType get() = when (this) {
    is Success -> content
    is Failure -> throw RuntimeException("Failure: ${content.toString()}")
}