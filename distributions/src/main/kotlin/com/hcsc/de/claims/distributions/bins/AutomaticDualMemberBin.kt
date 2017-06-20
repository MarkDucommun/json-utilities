package com.hcsc.de.claims.distributions.bins

import com.hcsc.de.claims.results.Result
import com.hcsc.de.claims.results.flatMap
import com.hcsc.de.claims.results.map

data class SuperAwesomeList(val underlyingList: List<Double>): List<Double> by underlyingList


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

    override fun splitDualBin(value: numberType): Result<String, SplitBinHolder<numberType, DualMemberBin<numberType, BinWithMembers<numberType>>>> {

        return binOne.split(value).flatMap { (upperBinOne, lowerBinOne) ->

            binTwo.split(value).map { (upperBinTwo, lowerBinTwo) ->

                SplitBinHolder<numberType, DualMemberBin<numberType, BinWithMembers<numberType>>>(
                        upper = AutomaticDualMemberBin(binOne = upperBinOne, binTwo = upperBinTwo, toType = toType),
                        lower = AutomaticDualMemberBin(binOne = lowerBinOne, binTwo = lowerBinTwo, toType = toType)
                )
            }
        }
    }
}