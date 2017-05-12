package com.hcsc.de.claims

fun <T, U> Collection<T>.ifNotEmptyOtherwiseNull(fn: Collection<T>.() -> U): U? {
    return if (this.isNotEmpty()) this.fn() else null
}

fun Double.ceiling() = Math.ceil(this)
