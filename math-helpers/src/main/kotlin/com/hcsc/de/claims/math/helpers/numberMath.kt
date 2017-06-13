package com.hcsc.de.claims.math.helpers

fun <T> List<T>.average(fn: T.() -> Int): Int = map(fn).averageInt()
