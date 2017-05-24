package com.hcsc.de.claims.results

fun <failureType, successType> List<Result<failureType, successType>>.traverse() : Result<failureType, List<successType>> {
    val list : Result<failureType, List<successType>> = Success(content = emptyList())
    return this.fold(list) { accum: Result<failureType, List<successType>>, result : Result<failureType, successType> ->
        when(accum) {
            is Success -> {
                when(result) {
                    is Success -> Success<failureType, List<successType>>(content = accum.content.plus(result.content))
                    is Failure ->  Failure<failureType, List<successType>>(content = result.content)
                }
            }
            is Failure -> Failure<failureType, List<successType>>(content = accum.content)
        }
    }
}