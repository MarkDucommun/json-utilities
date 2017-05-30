package com.hcsc.de.claims.distributions


interface UnknownDualMemberVariableBinWidthDistribution<out numberType : Number> : Distribution<numberType> {
    val numberOfBins: Int
    val bins: List<VariableDualMemberWidthBin<numberType>>
}

data class VariableDualMemberWidthBin<out numberType : Number>(
        val startValue: numberType,
        val endValue: numberType,
        val members: List<BinMember<numberType>>
) {
    val memberOneCount: Int = members.filter { it is BinMember.BinMemberOne }.size
    val memberTwoCount: Int = members.filter { it is BinMember.BinMemberTwo }.size
}

sealed class BinMember<out numberType : Number>(
        val value: numberType
) {

    class BinMemberOne<out numberType : Number>(value: numberType) : BinMember<numberType>(value = value)

    class BinMemberTwo<out numberType : Number>(value: numberType) : BinMember<numberType>(value = value)
}
