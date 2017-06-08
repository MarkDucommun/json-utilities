package com.hcsc.de.claims.distributions

interface UnknownVariableBinWidthDistribution<out numberType : Number>
    : BinDistribution<numberType> {
    val numberOfBins: Int
    override val bins: List<VariableWidthBin<numberType>>
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