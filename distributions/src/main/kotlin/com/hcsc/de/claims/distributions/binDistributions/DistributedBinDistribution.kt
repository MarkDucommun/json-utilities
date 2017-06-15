package com.hcsc.de.claims.distributions.binDistributions

import com.hcsc.de.claims.distributions.binDistributions.BinDistribution
import com.hcsc.de.claims.distributions.bins.DistributedBin
import java.util.*

data class DistributedBinDistribution<out numberType : Number>(
        override val average: numberType,
        override val minimum: numberType,
        override val maximum: numberType,
        override val mode: numberType,
        override val median: numberType,
        override val bins: List<DistributedBin<numberType>>
) : BinDistribution<numberType, DistributedBin<numberType>> {

    override val binCount: Int = bins.size

    private val random = Random()

    override fun random(): numberType {

        return bins[random.nextInt(bins.size)].random()
    }
}