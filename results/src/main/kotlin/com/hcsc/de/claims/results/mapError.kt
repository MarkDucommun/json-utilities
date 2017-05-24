package com.hcsc.de.claims.results

fun <failureType, newFailureType, successType> Result<failureType, successType>.mapError(
        transform: (failureType) -> newFailureType
): Result<newFailureType, successType> {

    return when (this) {
        is Success -> Success<newFailureType, successType>(content = this.content)
        is Failure -> Failure(transform(this.content))
    }
}
