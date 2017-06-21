package com.hcsc.de.claims.distributions.bins

import com.hcsc.de.claims.collection.helpers.*
import com.hcsc.de.claims.results.*

open class AutomaticBinWithMembers<numberType : Number>(
        rawMembers: NonEmptyList<numberType>,
        toType: Double.() -> numberType
) : AutomaticBinWithWidth<numberType>(
        size = rawMembers.size,
        rawStartValue = rawMembers.minimum(toType),
        rawEndValue = rawMembers.maximum(toType),
        toType = toType
), BinWithMembers<numberType> {

    override val members: NonEmptyList<numberType> = rawMembers

    override val average: numberType = rawMembers.average(toType)
    override val minimum: numberType = rawMembers.minimum(toType)
    override val maximum: numberType = rawMembers.maximum(toType)
    override val mode: numberType = rawMembers.simpleMode(toType)
    override val median: numberType = rawMembers.median(toType)

    override fun plus(other: BinWithMembers<numberType>): BinWithMembers<numberType> =
            AutomaticBinWithMembers(rawMembers = members.plus(other.members), toType = toType)

    override fun split(splitPoint: numberType): Result<SplitFailure, SplitBinHolder<numberType, BinWithMembers<numberType>>> {

        val membersAsDoubles = members.map(Number::toDouble)

        val splitPointDouble = splitPoint.toDouble()

        val upperResult = membersAsDoubles
                .filter { it > splitPointDouble }
                .mapError { UpperSplitFailure as SplitFailure } // TODO send this to Intellij

        val lowerResult = membersAsDoubles
                .filterNot { it > splitPointDouble }
                .mapError { LowerSplitFailure as SplitFailure }

        return zip(upperResult, lowerResult).map { (upperMembers, lowerMembers) ->

            val typedUpper = AutomaticBinWithMembers(rawMembers = upperMembers.map(toType), toType = toType)
            val typedLower = AutomaticBinWithMembers(rawMembers = lowerMembers.map(toType), toType = toType)

            SplitBinHolder<numberType, BinWithMembers<numberType>>(upper = typedUpper, lower = typedLower)
        }
    }

    override fun random(): numberType {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}