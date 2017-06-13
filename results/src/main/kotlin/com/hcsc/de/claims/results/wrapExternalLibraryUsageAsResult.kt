package com.hcsc.de.claims.results

fun <successType> wrapExternalLibraryUsageAsResult(
        defaultMessage: String = "Something went wrong!",
        fn: () -> successType
): Result<String, successType> = try {
    Success(fn.invoke())
} catch (e: Exception) {
    Failure(e.message ?: defaultMessage)
}