package com.hcsc.de.claims.distributions

import com.hcsc.de.claims.helpers.Result

interface Distribution<out numberType: Number> : Randomable<numberType> {
    val average: numberType
    val minimum: numberType
    val maximum: numberType
    val mode: numberType
    val median: numberType
}

interface Randomable<out numberType: Number> {

    fun random() : numberType
}

val List<Int>.distribution: Distribution<Int> get() {

    return this.normalIntdistribution
}