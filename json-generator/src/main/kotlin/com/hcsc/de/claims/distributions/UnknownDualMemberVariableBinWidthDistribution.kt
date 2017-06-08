package com.hcsc.de.claims.distributions


data class UnknownDualMemberVariableBinWidthDistribution<out numberType : Number>(
        override val bins: List<VariableDualMemberWidthBin<numberType>>,
        override val average: numberType,
        override val minimum: numberType,
        override val maximum: numberType,
        override val mode: numberType,
        override val median: numberType
) : BinDistribution<numberType> {

    val numberOfBins: Int = bins.size

    override fun random(): numberType {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

data class VariableDualMemberWidthBin<out numberType : Number>(
        val startValue: numberType,
        val endValue: numberType,
        val members: List<BinMember<numberType>>
) : Bin {
    val memberOneCount: Int = members.filter { it is BinMember.BinMemberOne }.size
    val memberTwoCount: Int = members.filter { it is BinMember.BinMemberTwo }.size
    override val count: Int = members.size
}

sealed class BinMember<out numberType : Number>(
        val value: numberType
) {

    class BinMemberOne<out numberType : Number>(value: numberType) : BinMember<numberType>(value = value)

    class BinMemberTwo<out numberType : Number>(value: numberType) : BinMember<numberType>(value = value)
}
