package com.hcsc.de.claims.distributions.binDistributions

import com.hcsc.de.claims.distributions.bins.Bin

open class AutomaticFixedWidthBinDistribution<out numberType : Number>(
        rawBins: List<Bin<numberType>>,
        override val average: numberType,
        override val minimum: numberType,
        override val maximum: numberType,
        override val mode: numberType,
        override val median: numberType,
        override val binWidth: numberType
) : FixedWidthBinDistribution<numberType> {

    override fun random(): numberType {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override val bins: List<Bin<numberType>> = rawBins

    override val binCount: Int = rawBins.size
}