package com.hcsc.de.claims.distributions

import com.hcsc.de.claims.math.helpers.median


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

    val range: Double = endValue.toDouble() - startValue.toDouble()

    val memberOnes: List<BinMember<numberType>> = members.filter { it is BinMember.BinMemberOne }
    val memberTwos: List<BinMember<numberType>> = members.filter { it is BinMember.BinMemberTwo }

    val membersValues: List<Double> = members.map { it.value }.map(Number::toDouble)

    val median: Double = membersValues.median()
    val average: Double = membersValues.average()

    val memberOneValues: List<Double> = memberOnes.map { it.value }.map(Number::toDouble)
    val memberTwoValues: List<Double> = memberTwos.map { it.value }.map(Number::toDouble)

    val memberOneCount: Int = memberOnes.size
    val memberTwoCount: Int = memberTwos.size

    override val count: Int = members.size
    override val identifyingCharacteristic: numberType = startValue
}

sealed class BinMember<out numberType : Number>(
        val value: numberType
) {

    class BinMemberOne<out numberType : Number>(value: numberType) : BinMember<numberType>(value = value)

    class BinMemberTwo<out numberType : Number>(value: numberType) : BinMember<numberType>(value = value)
}
