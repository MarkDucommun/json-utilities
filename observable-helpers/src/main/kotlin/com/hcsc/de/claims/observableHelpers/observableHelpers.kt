package com.hcsc.de.claims.observableHelpers

import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

fun <T> doOnThread(scheduler: Scheduler = Schedulers.trampoline(), fn: () -> T): Single<T> {
    return Single.just(Unit).subscribeOn(scheduler).map { fn() }
}

fun <T> doOnThreadAndFlatten(scheduler: Scheduler = Schedulers.trampoline(), fn: () -> Single<T>): Single<T> {
    return Single.just(Unit).subscribeOn(scheduler).flatMap { fn() }
}