package com.hcsc.de.claims.distributions.bins

interface BinWithWidth<numberType : Number> : NumericalBin<numberType> {

    val startValue: numberType

    val endValue: numberType

    val width: numberType

    fun plus(other: BinWithWidth<numberType>): BinWithWidth<numberType>
}