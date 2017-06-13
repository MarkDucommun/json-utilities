package com.hcsc.de.claims.distributions

import java.util.*

data class DistributedBinDistribution<out numberType : Number>(
        override val average: numberType,
        override val minimum: numberType,
        override val maximum: numberType,
        override val mode: numberType,
        override val median: numberType,
        override val bins: List<DistributedBin<numberType>>
) : BinDistribution<numberType> {

    private val random = Random()

    override fun random(): numberType {

        return bins[random.nextInt(bins.size)].random()
    }
}