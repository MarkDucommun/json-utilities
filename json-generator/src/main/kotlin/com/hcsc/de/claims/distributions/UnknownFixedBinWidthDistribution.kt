package com.hcsc.de.claims.distributions

interface UnknownFixedBinWidthDistribution<out numberType : Number> : BinDistribution<numberType> {
    val numberOfBins: Int
    val sizeOfBin: numberType
    override val bins: List<FixedWidthBin<numberType>>
}

data class FixedWidthBin<out numberType : Number>(
        val startValue: numberType,
        override val count: Int
) : Bin {
    override val identifyingCharacteristic: numberType = startValue
}
