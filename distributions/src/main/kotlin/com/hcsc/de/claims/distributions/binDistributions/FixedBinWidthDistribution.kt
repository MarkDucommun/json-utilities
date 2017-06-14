package com.hcsc.de.claims.distributions.binDistributions

import com.hcsc.de.claims.distributions.binDistributions.BinDistribution
import com.hcsc.de.claims.distributions.bins.Bin

interface FixedBinWidthDistribution<out numberType : Number> : BinDistribution<numberType, Bin<numberType>> {
    val numberOfBins: Int
    val sizeOfBin: numberType
    override val bins: List<Bin<numberType>>
}