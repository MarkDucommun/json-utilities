package com.hcsc.de.claims.distributions.binDistributions

import com.hcsc.de.claims.distributions.bins.Bin

interface FixedWidthBinDistribution<out numberType : Number> : BinDistribution<numberType, Bin<numberType>> {

    val binWidth: numberType
}