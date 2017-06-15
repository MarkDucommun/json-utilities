package com.hcsc.de.claims.distributions.bins

interface NumericalBin<out numberType : Number> : Bin<numberType> {

    val Double.asType: numberType
}