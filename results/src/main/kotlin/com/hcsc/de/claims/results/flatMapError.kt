package com.hcsc.de.claims.results

fun <failureType, newFailureType, successType> Result<failureType, successType>.flatMapError(
        transform: (failureType) -> Result<newFailureType, successType>
): Result<newFailureType, successType> {

    return when (this) {
        is Success -> Success<newFailureType, successType>(content = this.content)
        is Failure -> transform(this.content)
    }
}