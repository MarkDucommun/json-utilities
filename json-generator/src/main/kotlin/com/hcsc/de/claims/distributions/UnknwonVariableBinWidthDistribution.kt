package com.hcsc.de.claims.distributions

import java.util.*

data class UnknownVariableBinWidthDistribution<out numberType : Number>(
        override val average: numberType,
        override val minimum: numberType,
        override val maximum: numberType,
        override val mode: numberType,
        override val median: numberType,
        override val bins: List<VariableWidthBin<numberType>>
)
    : BinDistribution<numberType> {
    val numberOfBins: Int = bins.size

    private val random = Random()

    override fun random(): numberType {

        val index = random.nextInt(bins.size)

        val bin = bins[index]

        return bin.members[random.nextInt(bin.members.size)]
    }
}

data class VariableWidthBin<out numberType : Number>(
        val startValue: numberType,
        val endValue: numberType,
        val members: List<numberType>
) : Bin {

    override val count: Int = members.size

    val width: Int = endValue.toInt() - startValue.toInt()
}

class DistributedBin<out numberType : Number>(
        override val count: Int,
        val startValue: numberType,
        private val distribution: Distribution<numberType>,
        val pValue: Double
) : Bin, Randomable<numberType> {

    override fun random(): numberType {
        return distribution.random()
    }
}