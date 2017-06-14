package com.hcsc.de.claims.results

data class TimeAndResult<failureType, successType>(
        val result: Result<failureType, successType>,
        val elapsedTimeMillis: Double,
        val elapsedTimeNanos: Long
)

fun <failureType, successType> time(fn: () -> Result<failureType, successType>): TimeAndResult<failureType, successType> {

    val start = System.nanoTime()

    val result = fn.invoke()

    val end = System.nanoTime()

    return TimeAndResult(
            result = result,
            elapsedTimeMillis = (end.toDouble() - start.toDouble()) / 1000000.0,
            elapsedTimeNanos = end - start
    )
}