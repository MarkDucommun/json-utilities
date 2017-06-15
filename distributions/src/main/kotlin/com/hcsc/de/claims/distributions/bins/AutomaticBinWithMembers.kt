package com.hcsc.de.claims.distributions.bins

import com.hcsc.de.claims.math.helpers.median
import com.hcsc.de.claims.math.helpers.mode

open class AutomaticBinWithMembers<numberType : Number>(
        rawMembers: List<numberType>,
        toType: Double.() -> numberType
) : AutomaticBinWithWidth<numberType>(
        size = rawMembers.size,
        rawStartValue = (rawMembers.map(Number::toDouble).min() ?: 0.0).toType(),
        rawEndValue = (rawMembers.map(Number::toDouble).max() ?: 0.0).toType(),
        toType = toType
), BinWithMembers<numberType> {

    private val rawDoubleMembers = rawMembers.map(Number::toDouble)
    override val doubleMembers: List<Double> = rawDoubleMembers
    override val members: List<numberType> = rawMembers

    override val average: numberType = rawDoubleMembers.average().toType()
    override val minimum: numberType = (rawDoubleMembers.min() ?: 0.0).toType()
    override val maximum: numberType = (rawDoubleMembers.max() ?: 0.0).toType()
    override val mode: numberType = rawDoubleMembers.mode().toType()
    override val median: numberType = rawDoubleMembers.median().toType()

    override fun plus(other: BinWithMembers<numberType>): BinWithMembers<numberType> =
            AutomaticBinWithMembers(rawMembers = members.plus(other.members), toType = toType)

    override fun split(value: numberType): SplitBinHolder<numberType, BinWithMembers<numberType>> =
            splitByDouble(value.toDouble())

    override fun splitByDouble(value: Double): SplitBinHolder<numberType, BinWithMembers<numberType>> {

        val upperMembers = doubleMembers.filter { it > value }.map(toType)
        val lowerMembers = doubleMembers.filterNot { it > value }.map(toType)

        return SplitBinHolder(
                upper = AutomaticBinWithMembers(rawMembers = upperMembers, toType = toType),
                lower = AutomaticBinWithMembers(rawMembers = lowerMembers, toType = toType)
        )
    }

    override fun random(): numberType {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

