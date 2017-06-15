package com.hcsc.de.claims.distributions.bins

open class AutomaticDualMemberBin<numberType : Number>(
        override val binOne: BinWithMembers<numberType>,
        override val binTwo: BinWithMembers<numberType>,
        toType: Double.() -> numberType
) : AutomaticBinWithMembers<numberType>(
        rawMembers = binOne.plus(binTwo).members,
        toType = toType
), DualMemberBin<numberType, BinWithMembers<numberType>> {

    override fun plus(
            other: DualMemberBin<numberType, BinWithMembers<numberType>>
    ): DualMemberBin<numberType, BinWithMembers<numberType>> {

        return AutomaticDualMemberBin(
                binOne = binOne.plus(other.binOne),
                binTwo = binTwo.plus(other.binTwo),
                toType = toType
        )
    }

    override fun split(value: numberType): SplitBinHolder<numberType, DualMemberBin<numberType, BinWithMembers<numberType>>> {

        return splitByDouble(value.toDouble())
    }

    override fun splitByDouble(value: Double): SplitBinHolder<numberType, DualMemberBin<numberType, BinWithMembers<numberType>>> {

        val binOneSplit = binOne.splitByDouble(value)
        val binTwoSplit = binTwo.splitByDouble(value)

        return SplitBinHolder(
                upper = AutomaticDualMemberBin(binOne = binOneSplit.upper, binTwo = binTwoSplit.upper, toType = toType),
                lower = AutomaticDualMemberBin(binOne = binOneSplit.lower, binTwo = binTwoSplit.lower, toType = toType)
        )
    }
}