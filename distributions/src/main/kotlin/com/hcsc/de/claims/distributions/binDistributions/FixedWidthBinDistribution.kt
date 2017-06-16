package com.hcsc.de.claims.distributions.binDistributions

import com.hcsc.de.claims.distributions.bins.Bin

interface FixedWidthBinDistribution<out numberType : Number, out binType: Bin<numberType>> : BinDistribution<numberType, binType> {

    val binWidth: numberType
}