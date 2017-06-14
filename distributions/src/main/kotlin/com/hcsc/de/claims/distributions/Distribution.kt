package com.hcsc.de.claims.distributions

interface Distribution<out numberType: Number> : Randomable<numberType> {
    val average: numberType
    val minimum: numberType
    val maximum: numberType
    val mode: numberType
    val median: numberType
}