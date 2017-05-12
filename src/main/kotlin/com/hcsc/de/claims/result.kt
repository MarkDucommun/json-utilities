package com.hcsc.de.claims

sealed class Result<failureType, successType>

data class Success<failureType, successType>(
        val content: successType
) : Result<failureType, successType>()

data class Failure<failureType, successType>(
        val content: failureType
) : Result<failureType, successType>()