package com.hcsc.de.claims.math.helpers

import org.apache.commons.math3.stat.StatUtils

fun Double.ceiling() = Math.ceil(this)

fun Double.ceilingOnEven() = if (this.toInt() % 2 == 0) this.ceiling() else this

// TODO this is breaking sometimes
fun List<Double>.median(): Double {

    val sorted = sorted()
    val middleIndex = size / 2

    return if (sorted.size % 2 == 0) {
        (sorted[middleIndex] + sorted[middleIndex - 1]) / 2
    } else {
        sorted[middleIndex]
    }
}

fun List<Double>.mode(): Double = StatUtils.mode(toDoubleArray()).max()?.ceilingOnEven() ?: 0.0

fun Double.square() = this * this

fun Double.sqrt() = Math.sqrt(this)