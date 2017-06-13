package com.hcsc.de.claims.math.helpers

import org.apache.commons.math3.stat.StatUtils

fun List<Int>.averageInt() = average().ceilingOnEven().toInt()

fun List<Int>.medianInt(): Int {

    val sorted = sorted()
    val middleIndex = size / 2

    return if (sorted.size % 2 == 0) {
        (sorted[middleIndex] + sorted[middleIndex - 1]) / 2
    } else {
        sorted[middleIndex]
    }
}

fun List<Int>.modeInt(): Int = StatUtils.mode(this.map(Int::toDouble).toDoubleArray()).max()?.ceilingOnEven()?.toInt() ?: 0

fun Int.square() = this * this

fun Int.sqrt() = Math.sqrt(this.toDouble())