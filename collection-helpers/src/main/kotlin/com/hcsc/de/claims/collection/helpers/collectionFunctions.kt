package com.hcsc.de.claims.collection.helpers

fun <T, U> Collection<T>.ifNotEmptyOtherwiseNull(fn: Collection<T>.() -> U): U? {
    return if (this.isNotEmpty()) this.fn() else null
}

fun List<Int>.filterLessThanInt(minimum: Int?): List<Int> = filterByComparable(minimum, { this < it })

fun List<Int>.filterNotLessThanInt(minimum: Int?): List<Int> = filterNotByComparable(minimum, { this < it })

fun List<Int>.filterLessThanOrEqualToInt(minimum: Int?): List<Int> = filterByComparable(minimum, { this <= it })

fun List<Int>.filterNotLessThanOrEqualToInt(minimum: Int?): List<Int> = filterNotByComparable(minimum, { this <= it })

fun List<Int>.filterGreaterThanInt(maximum: Int?): List<Int> = filterByComparable(maximum, { this > it })

fun List<Int>.filterNotGreaterThanInt(maximum: Int?): List<Int> = filterNotByComparable(maximum, { this > it })

fun List<Int>.filterGreaterThanOrEqualToInt(maximum: Int?): List<Int> = filterByComparable(maximum, { this >= it })

fun List<Int>.filterNotGreaterThanOrEqualToInt(maximum: Int?): List<Int> = filterNotByComparable(maximum, { this >= it })

fun List<Int>.filterByComparable(value: Int?, compareFn: Int.(Int) -> Boolean): List<Int> =
        value?.let { filter { it.compareFn(value) } } ?: this

fun List<Int>.filterNotByComparable(value: Int?, compareFn: Int.(Int) -> Boolean): List<Int> =
        value?.let { filterNot { it.compareFn(value)} } ?: this



fun List<Double>.filterLessThan(minimum: Double?): List<Double> = filterByComparable(minimum, { this < it })

fun List<Double>.filterNotLessThan(minimum: Double?): List<Double> = filterNotByComparable(minimum, { this < it })

fun List<Double>.filterLessThanOrEqualTo(minimum: Double?): List<Double> = filterByComparable(minimum, { this <= it })

fun List<Double>.filterNotLessThanOrEqualTo(minimum: Double?): List<Double> = filterNotByComparable(minimum, { this <= it })

fun List<Double>.filterGreaterThan(maximum: Double?): List<Double> = filterByComparable(maximum, { this > it })

fun List<Double>.filterNotGreaterThan(maximum: Double?): List<Double> = filterNotByComparable(maximum, { this > it })

fun List<Double>.filterGreaterThanOrEqualTo(maximum: Double?): List<Double> = filterByComparable(maximum, { this >= it })

fun List<Double>.filterNotGreaterThanOrEqualTo(maximum: Double?): List<Double> = filterNotByComparable(maximum, { this >= it })

fun List<Double>.filterByComparable(value: Double?, compareFn: Double.(Double) -> Boolean): List<Double> =
        value?.let { filter { it.compareFn(value) } } ?: this

fun List<Double>.filterNotByComparable(value: Double?, compareFn: Double.(Double) -> Boolean): List<Double> =
        value?.let { filterNot { it.compareFn(value)} } ?: this