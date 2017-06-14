package com.hcsc.de.claims.distributions.bins

import com.hcsc.de.claims.distributions.Distribution
import com.hcsc.de.claims.math.helpers.median
import com.hcsc.de.claims.math.helpers.mode

interface NumericalBin<out numberType : Number> : Bin<numberType> {

    val Double.asType: numberType
}

interface BinWithWidth<numberType : Number> : NumericalBin<numberType> {

    val startValue: numberType

    val endValue: numberType

    val width: numberType

    fun plus(other: BinWithWidth<numberType>): BinWithWidth<numberType>
}

open class AutomaticBinWithWidth<numberType : Number>(
        override val size: Int,
        rawStartValue: numberType,
        rawEndValue: numberType,
        val toType: Double.() -> numberType
) : BinWithWidth<numberType> {

    override val Double.asType: numberType get() = toType()

    override val startValue: numberType = rawStartValue

    private val doubleStartValue: Double = rawStartValue.toDouble()

    override val endValue: numberType = rawEndValue

    private val doubleEndValue: Double = rawEndValue.toDouble()

    override val identifyingCharacteristic: numberType = rawStartValue

    override val width: numberType = (doubleEndValue - doubleStartValue).toType()

    override fun plus(other: BinWithWidth<numberType>): BinWithWidth<numberType> {

        val newStartValue = if (doubleStartValue < other.startValue.toDouble()) startValue else other.startValue
        val newEndValue = if (doubleEndValue > other.endValue.toDouble()) endValue else other.endValue

        return AutomaticBinWithWidth(
                size = size + other.size,
                rawStartValue = newStartValue,
                rawEndValue = newEndValue,
                toType = toType
        )
    }
}

interface BinWithMembers<memberType : Number> : BinWithWidth<memberType>, Distribution<memberType> {

    val members: List<memberType>

    val doubleMembers: List<Double>

    fun plus(other: BinWithMembers<memberType>): BinWithMembers<memberType>

    fun split(value: memberType): SplitBinHolder<memberType, BinWithMembers<memberType>>

    fun split(value: Double): SplitBinHolder<memberType, BinWithMembers<memberType>>
}

interface DualMemberBin<memberType : Number, binType : BinWithMembers<memberType>> : BinWithMembers<memberType> {

    val binOne: binType

    val binTwo: binType

    fun plus(other: DualMemberBin<memberType, binType>): DualMemberBin<memberType, binType>

    override fun split(value: memberType): SplitBinHolder<memberType, DualMemberBin<memberType, BinWithMembers<memberType>>>

    override fun split(value: Double): SplitBinHolder<memberType, DualMemberBin<memberType, BinWithMembers<memberType>>>
}

open class AutomaticBinWithMembers<numberType : Number>(

        rawMembers: List<numberType>,
        toType: Double.() -> numberType

) : AutomaticBinWithWidth<numberType>(
        size = rawMembers.size,
        rawStartValue = (rawMembers.map(Number::toDouble).min() ?: 0.0).toType(),
        rawEndValue = (rawMembers.map(Number::toDouble).max() ?: 0.0).toType(),
        toType = toType
), BinWithMembers<numberType> {

    override fun split(value: numberType): SplitBinHolder<numberType, BinWithMembers<numberType>> {

        return split(value.toDouble())
    }

    override fun split(value: Double): SplitBinHolder<numberType, BinWithMembers<numberType>> {

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

    override val members: List<numberType> = rawMembers

    private val rawDoubleMembers = rawMembers.map(Number::toDouble)

    override val doubleMembers: List<Double> = rawDoubleMembers

    override val average: numberType = rawDoubleMembers.average().toType()
    override val minimum: numberType = (rawDoubleMembers.min() ?: 0.0).toType()
    override val maximum: numberType = (rawDoubleMembers.max() ?: 0.0).toType()
    override val mode: numberType = rawDoubleMembers.mode().toType()
    override val median: numberType = rawDoubleMembers.median().toType()

    override fun plus(other: BinWithMembers<numberType>): BinWithMembers<numberType> =
            AutomaticBinWithMembers(rawMembers = members.plus(other.members), toType = toType)
}

data class SplitBinHolder<numberType: Number, out binType: BinWithMembers<numberType>>(
        val upper: binType,
        val lower: binType
)

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

        return split(value.toDouble())
    }

    override fun split(value: Double): SplitBinHolder<numberType, DualMemberBin<numberType, BinWithMembers<numberType>>> {

        val binOneSplit = binOne.split(value)
        val binTwoSplit = binTwo.split(value)

        return SplitBinHolder(
                upper = AutomaticDualMemberBin(binOne = binOneSplit.upper, binTwo = binTwoSplit.upper, toType = toType),
                lower = AutomaticDualMemberBin(binOne = binOneSplit.lower, binTwo = binTwoSplit.lower, toType = toType)
        )
    }
}

data class DoubleBinWithMembers(
        override val members: List<Double>
) : AutomaticBinWithMembers<Double>(
        rawMembers = members,
        toType = Double::toDouble
)

data class IntBinWithMembers(
        override val members: List<Int>
) : AutomaticBinWithMembers<Int>(
        rawMembers = members,
        toType = Double::toInt
)

data class DoubleDualMemberBin(
        override val binOne: BinWithMembers<Double>,
        override val binTwo: BinWithMembers<Double>
) : AutomaticDualMemberBin<Double>(
        binOne = binOne,
        binTwo = binTwo,
        toType = Double::toDouble
)