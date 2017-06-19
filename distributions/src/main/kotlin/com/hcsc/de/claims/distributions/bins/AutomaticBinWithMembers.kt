package com.hcsc.de.claims.distributions.bins

import com.hcsc.de.claims.collection.helpers.*
import com.hcsc.de.claims.math.helpers.median
import com.hcsc.de.claims.math.helpers.mode
import com.hcsc.de.claims.results.Result
import com.hcsc.de.claims.results.flatMap
import com.hcsc.de.claims.results.map

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

    override fun split(value: numberType): Result<String, SplitBinHolder<numberType, BinWithMembers<numberType>>> {

        return members.filter { it.toDouble() > value.toDouble() }.flatMap { upperMembers ->

            members.filterNot { it.toDouble() > value.toDouble() }.map { lowerMembers ->

                SplitBinHolder<numberType, BinWithMembers<numberType>>(
                        upper = AutomaticBinWithMembers(rawMembers = upperMembers, toType = toType),
                        lower = AutomaticBinWithMembers(rawMembers = lowerMembers, toType = toType)
                )
            }
        }
    }

    override fun random(): numberType {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

