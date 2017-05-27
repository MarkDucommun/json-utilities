package com.hcsc.de.claims.jsonSizing

import com.hcsc.de.claims.helpers.averageInt
import com.hcsc.de.claims.helpers.medianInt
import com.hcsc.de.claims.helpers.modeInt

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

data class UnknownDualMemberVariableBinWidthIntDistribution(
        override val average: Int,
        override val minimum: Int,
        override val maximum: Int,
        override val median: Int,
        override val mode: Int,
        override val bins: List<VariableDualMemberWidthBin<Int>>
) : UnknownDualMemberVariableBinWidthDistribution<Int> {
    override val numberOfBins: Int = bins.size

    override fun random(): Int {
        TODO("not implemented")
    }
}

data class DistributionPair<out numberType : Number>(
        val one: List<numberType>,
        val two: List<numberType>
)

fun DistributionPair<Int>.unknownDualMemberVariableBinWidthDistribution(
        minimumBinCount: Int = 5,
        rangeMinimum: Int? = null,
        rangeMaximum: Int? = null
): UnknownDualMemberVariableBinWidthDistribution<Int> {

    val (listOne, listTwo) = this

    val sortedOne: List<Int> = listOne.sorted()
    val sortedTwo: List<Int> = listTwo.sorted()

    val listOneBinMembers = sortedOne.map { BinMember.BinMemberOne(it) }
    val listTwoBinMembers = sortedTwo.map { BinMember.BinMemberTwo(it) }

    val combined = listOneBinMembers.plus(listTwoBinMembers)

    val sortedAndFilteredByMinimum = rangeMinimum
            ?.let { combined.filterNot { it.value < rangeMinimum } }
            ?: combined

    val sortedAndFilteredByMinimumAndMaximum = rangeMaximum
            ?.let { sortedAndFilteredByMinimum.filterNot { it.value > rangeMaximum } }
            ?: sortedAndFilteredByMinimum

    val observedMinimum = sortedAndFilteredByMinimumAndMaximum.map { it.value }.min() ?: 0
    val observedMaximum = sortedAndFilteredByMinimumAndMaximum.map { it.value }.max() ?: 0

    return UnknownDualMemberVariableBinWidthIntDistribution(
            average = sortedAndFilteredByMinimumAndMaximum.map { it.value }.averageInt(),
            minimum = observedMinimum,
            maximum = observedMaximum,
            median = sortedAndFilteredByMinimumAndMaximum.map { it.value }.medianInt(),
            mode = sortedAndFilteredByMinimumAndMaximum.map { it.value }.modeInt(),
            bins = sortedAndFilteredByMinimumAndMaximum.asBin.maximizeBins(minimumBinCount)
    )
}

val UnknownDualMemberVariableBinWidthDistribution<Int>
        .asTwoDistributions : Pair<UnknownVariableBinWidthDistribution<Int>, UnknownVariableBinWidthDistribution<Int>> get() {

    val firstBins = this.bins.map {
        val members = it.members.filter { it is BinMember.BinMemberOne<Int> }.map { it.value }
        VariableWidthBin(startValue = it.startValue, endValue = it.endValue, members = members)
    }

    val firstMembers = firstBins.flatMap { it.members }

    val secondBins = this.bins.map {
        val members = it.members.filter { it is BinMember.BinMemberTwo<Int> }.map { it.value }
        VariableWidthBin(startValue = it.startValue, endValue = it.endValue, members = members)
    }

    val secondMembers = secondBins.flatMap { it.members }

    return Pair(
            UnknownIntVariableBinWidthDistribution(
                    bins = firstBins,
                    average = firstMembers.averageInt(),
                    minimum = firstMembers.min() ?: 0,
                    maximum = firstMembers.max() ?: 0,
                    median = firstMembers.medianInt(),
                    mode = firstMembers.modeInt()
            ),
            UnknownIntVariableBinWidthDistribution(
                    bins = secondBins,
                    average = secondMembers.averageInt(),
                    minimum = secondMembers.min() ?: 0,
                    maximum = secondMembers.max() ?: 0,
                    median = secondMembers.medianInt(),
                    mode = secondMembers.modeInt()
            )
    )
}


private fun VariableDualMemberWidthBin<Int>.maximizeBins(binCount: Int): List<VariableDualMemberWidthBin<Int>> {

    val median = members.map { it.value }.averageInt()

    val lower = members.filterNot { it.value >= median }

    val upper = members.filter { it.value >= median }

    return if (endValue - startValue > 0
            && lower.filter { it is BinMember.BinMemberOne }.count() > binCount
            && lower.filter { it is BinMember.BinMemberTwo }.count() > binCount
            && upper.filter { it is BinMember.BinMemberOne }.count() > binCount
            && upper.filter { it is BinMember.BinMemberTwo }.count() > binCount) {

        listOf(lower.asBin.maximizeBins(binCount), upper.asBin.maximizeBins(binCount)).flatten()
    } else {
        listOf(this)
    }
}

private val List<BinMember<Int>>.asBin: VariableDualMemberWidthBin<Int>
    get() = VariableDualMemberWidthBin(
            startValue = this.map { it.value }.min() ?: 0,
            endValue = this.map { it.value }.max() ?: 0,
            members = this.sortedBy { it.value }
    )