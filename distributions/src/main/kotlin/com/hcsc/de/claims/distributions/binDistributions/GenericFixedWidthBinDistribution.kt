package com.hcsc.de.claims.distributions.binDistributions

import com.hcsc.de.claims.distributions.bins.Bin

data class GenericFixedWidthBinDistribution<out numberType : Number>(
        override val bins: List<Bin<numberType>>,
        override val average: numberType,
        override val minimum: numberType,
        override val maximum: numberType,
        override val mode: numberType,
        override val median: numberType,
        override val binWidth: numberType
) : FixedWidthBinDistribution<numberType, Bin<numberType>> {

    override fun random(): numberType {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override val binCount: Int = bins.size
}