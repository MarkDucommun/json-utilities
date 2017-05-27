package com.hcsc.de.claims.helpers

import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.apache.commons.math3.stat.StatUtils

fun <T, U> Collection<T>.ifNotEmptyOtherwiseNull(fn: Collection<T>.() -> U): U? {
    return if (this.isNotEmpty()) this.fn() else null
}

fun Double.ceiling() = Math.ceil(this)

fun Double.ceilingOnEven() = if (this.toInt() % 2 == 0) this.ceiling() else this

fun <T> List<T>.average(fn: T.() -> Int): Int = map(fn).averageInt()

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

fun List<Double>.median(): Double {

    val sorted = sorted()
    val middleIndex = size / 2

    return if (sorted.size % 2 == 0) {
        (sorted[middleIndex] + sorted[middleIndex - 1]) / 2
    } else {
        sorted[middleIndex]
    }
}

fun List<Int>.modeInt(): Int = StatUtils.mode(this.map(Int::toDouble).toDoubleArray()).max()?.ceilingOnEven()?.toInt() ?: 0

fun List<Double>.mode(): Double = StatUtils.mode(toDoubleArray()).max()?.ceilingOnEven() ?: 0.0

fun Double.square() = this * this

fun Int.square() = this * this

fun Int.sqrt() = Math.sqrt(this.toDouble())

fun Double.sqrt() = Math.sqrt(this)

fun <T> doOnThread(scheduler: Scheduler = Schedulers.trampoline(), fn: () -> T): Single<T> {
    return Single.just(Unit).subscribeOn(scheduler).map { fn() }
}

fun <T> doOnThreadAndFlatten(scheduler: Scheduler = Schedulers.trampoline(), fn: () -> Single<T>): Single<T> {
    return Single.just(Unit).subscribeOn(scheduler).flatMap { fn() }
}