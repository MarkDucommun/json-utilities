package com.hcsc.de.claims.helpers

import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

fun <T, U> Collection<T>.ifNotEmptyOtherwiseNull(fn: Collection<T>.() -> U): U? {
    return if (this.isNotEmpty()) this.fn() else null
}

fun Double.ceiling() = Math.ceil(this)

fun Double.ceilingOnEven() = if (this.toInt() % 2 == 0) this.ceiling() else this

fun <T> List<T>.average(fn: T.() -> Int): Int = map(fn).averageInt()

fun List<Int>.averageInt() = average().ceilingOnEven().toInt()

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