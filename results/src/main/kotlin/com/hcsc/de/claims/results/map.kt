package com.hcsc.de.claims.results

fun <failureType, successType, newSuccessType> Result<failureType, successType>.map(
        transform: (successType) -> newSuccessType
): Result<failureType, newSuccessType> {

    return when (this) {
        is Success -> Success(transform(this.content))
        is Failure -> Failure<failureType, newSuccessType>(content = this.content)
    }
}