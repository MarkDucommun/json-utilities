package com.hcsc.de.claims.jsonSizing

interface Distribution<out numberType: Number> {
    val average: numberType
    val minimum: numberType
    val maximum: numberType
    val mode: numberType
    val median: numberType

    fun random() : numberType
}

val List<Int>.distribution: Distribution<Int> get() {

    return this.normalIntdistribution
}