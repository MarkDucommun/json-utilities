package com.hcsc.de.claims.results

fun <successType> wrapExternalLibraryUsageAsResult(
        defaultMessage: String = "Something went wrong!",
        fn: () -> successType
): Result<String, successType> = try {
    Success(fn.invoke())
} catch (e: Throwable) {
    Failure(e.message ?: defaultMessage)
}

fun <successType> wrapExternalLibraryUsageAsResultWithFailureMessage(
        message: String,
        fn: () -> successType
): Result<String, successType> = try {
    Success(fn.invoke())
} catch (e: Throwable) {
    Failure(message)
}