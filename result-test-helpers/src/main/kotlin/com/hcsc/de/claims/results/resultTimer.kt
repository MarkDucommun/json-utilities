package com.hcsc.de.claims.results

data class TimeAndResult<failureType, successType>(
        val result: Result<failureType, successType>,
        val elapsedTime: Double
)

fun <failureType, successType> time(fn: () -> Result<failureType, successType>): TimeAndResult<failureType, successType> {

    val start = System.nanoTime()

    val result = fn.invoke()

    return TimeAndResult(
            result = result,
            elapsedTime = (System.nanoTime().toDouble() - start.toDouble()) / 1000000.0
    )
}