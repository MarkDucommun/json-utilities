package com.hcsc.de.claims.results

fun <failureType, successType, newSuccessType> Result<failureType, successType>.flatMap(
        transform: (successType) -> Result<failureType, newSuccessType>
): Result<failureType, newSuccessType> {

    return when (this) {
        is Success -> transform(this.content)
        is Failure -> Failure<failureType, newSuccessType>(content = this.content)
    }
}