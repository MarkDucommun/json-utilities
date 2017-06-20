package com.hcsc.de.claims.results

fun <failureType, A, B> zip(first: Result<failureType, A>, second: Result<failureType, B>): Result<failureType, Pair<A, B>> {

    return first.flatMap { firstContent -> second.map { secondContent -> firstContent to secondContent  } }
}