package com.hcsc.de.claims.distributions

import com.hcsc.de.claims.helpers.averageInt
import com.hcsc.de.claims.helpers.medianInt
import com.hcsc.de.claims.helpers.modeInt
import java.util.*

interface UnknownVariableBinWidthDistribution<out numberType : Number> : BinDistribution<numberType> {
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