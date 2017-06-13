package com.hcsc.de.claims.results

sealed class Result<failureType, successType>

data class Success<failureType, successType>(
        val content: successType
) : Result<failureType, successType>()

data class Failure<failureType, successType>(
        val content: failureType
) : Result<failureType, successType>()

val EMPTY_SUCCESS = Success<String, Unit>(Unit)