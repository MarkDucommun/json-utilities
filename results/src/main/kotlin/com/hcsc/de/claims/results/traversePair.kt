package com.hcsc.de.claims.results

fun <failureType, successTypeOne, successTypeTwo> Pair<Result<failureType, successTypeOne>, Result<failureType, successTypeTwo>>
        .traverse() : Result<failureType, Pair<successTypeOne, successTypeTwo>>{

    val (one, two) = this

    return when (one) {
        is Success -> when (two) {
            is Success -> Success(Pair(one.content, two.content))
            is Failure -> Failure(two.content)
        }
        is Failure -> Failure(one.content)
    }
}