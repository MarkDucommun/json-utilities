@file:JvmName("UnknownDualMemberVariableBinWidthDistributionKt")

package com.hcsc.de.claims.distributions

import com.hcsc.de.claims.helpers.*

data class DistributionPair<out numberType : Number>(
        val one: List<numberType>,
        val two: List<numberType>
)

fun <numberType: Number> DistributionPair<numberType>.unknownDualMemberVariableBinWidthDistribution(
        minimumBinCount: Int = 5,
        rangeMinimum: numberType? = null,
        rangeMaximum: numberType? = null
): UnknownDualMemberVariableBinWidthDistribution<Double> {

    val (listOne, listTwo) = this

    val sortedOne = listOne.map(Number::toDouble).sorted()
    val sortedTwo = listTwo.map(Number::toDouble).sorted()

    val listOneBinMembers = sortedOne.map { BinMember.BinMemberOne(it) }
    val listTwoBinMembers = sortedTwo.map { BinMember.BinMemberTwo(it) }

    val combined = listOneBinMembers.plus(listTwoBinMembers)

    val sortedAndFilteredByMinimum = rangeMinimum
            ?.let { combined.filterNot { it.value < rangeMinimum.toDouble() } }
            ?: combined

    val sortedAndFilteredByMinimumAndMaximum = rangeMaximum
            ?.let { sortedAndFilteredByMinimum.filterNot { it.value > rangeMaximum.toDouble() } }
            ?: sortedAndFilteredByMinimum

    val observedMinimum = sortedAndFilteredByMinimumAndMaximum.map { it.value }.min() ?: 0.0
    val observedMaximum = sortedAndFilteredByMinimumAndMaximum.map { it.value }.max() ?: 0.0

    return UnknownDualMemberVariableBinWidthDistribution(
            average = sortedAndFilteredByMinimumAndMaximum.map { it.value }.average(),
            minimum = observedMinimum,
            maximum = observedMaximum,
            median = sortedAndFilteredByMinimumAndMaximum.map { it.value }.median(),
            mode = sortedAndFilteredByMinimumAndMaximum.map { it.value }.mode(),
            bins = sortedAndFilteredByMinimumAndMaximum.asDoubleBin.maximizeDoubleBins(minimumBinCount)
    )
}

val <numberType: Number> UnknownDualMemberVariableBinWidthDistribution<numberType>
        .asTwoDistributions : Pair<UnknownVariableBinWidthDistribution<Double>, UnknownVariableBinWidthDistribution<Double>> get() {

    val firstBins = this.bins.map {
        val members = it.members.filter { it is BinMember.BinMemberOne<numberType> }.map { it.value.toDouble() }
        VariableWidthBin(startValue = it.startValue.toDouble(), endValue = it.endValue.toDouble(), members = members)
    }

    val firstMembers = firstBins.flatMap { it.members }

    val secondBins: List<VariableWidthBin<Double>> = this.bins.map {
        val members = it.members.filter { it is BinMember.BinMemberTwo<numberType> }.map { it.value.toDouble() }
        VariableWidthBin(startValue = it.startValue.toDouble(), endValue = it.endValue.toDouble(), members = members)
    }

    val secondMembers = secondBins.flatMap { it.members }

    return Pair(
            UnknownVariableBinWidthDistribution(
                    bins = firstBins,
                    average = firstMembers.average(),
                    minimum = firstMembers.min() ?: 0.0,
                    maximum = firstMembers.max() ?: 0.0,
                    median = firstMembers.median(),
                    mode = firstMembers.mode()
            ),
            UnknownVariableBinWidthDistribution(
                    bins = secondBins,
                    average = secondMembers.average(),
                    minimum = secondMembers.min() ?: 0.0,
                    maximum = secondMembers.max() ?: 0.0,
                    median = secondMembers.median(),
                    mode = secondMembers.mode()
            )
    )
}

private fun VariableDualMemberWidthBin<Double>.maximizeDoubleBins(binCount: Int): List<VariableDualMemberWidthBin<Double>> {

    return if (endValue - startValue > 0) {

        val rangeHolder = RangeHolder(low = startValue, middle = (endValue + startValue) / 2, high = endValue)

        val finalRangeHolder = List(5) { it }.fold(rangeHolder) { rangeHolder, _ ->

            if (rangeHolder.isIncomplete) {

                val lower = members.filterNot { it.value >= rangeHolder.middle }

                val upper = members.filter { it.value >= rangeHolder.middle }

                if (lower.isBinnable(binCount) && upper.isBinnable(binCount)) {
                    rangeHolder.copy(isFinalBin = false, isIncomplete = false)
                } else if (lower.isBinnable(binCount)) {
                    rangeHolder.copy(high = rangeHolder.middle, middle = (rangeHolder.middle + rangeHolder.low) / 2)
                } else if (upper.isBinnable(binCount)) {
                    rangeHolder.copy(low = rangeHolder.middle, middle = (rangeHolder.high + rangeHolder.middle) / 2)
                } else {
                    rangeHolder.copy(isFinalBin = true, isIncomplete = false)
                }
            } else {
                rangeHolder
            }
        }

        if (finalRangeHolder.isFinalBin) {
            listOf(this)
        } else {
            val lower = members.filterNot { it.value >= rangeHolder.middle }.asDoubleBin.maximizeDoubleBins(binCount)

            val upper = members.filter { it.value >= rangeHolder.middle }.asDoubleBin.maximizeDoubleBins(binCount)

            lower.plus(upper)
        }
    } else {

        listOf(this)
    }
}

private fun <numberType: Number> List<BinMember<numberType>>.isBinnable(count: Int): Boolean {
    return filter { it is BinMember.BinMemberOne }.count() > count
            && filter { it is BinMember.BinMemberTwo }.count() > count
}

data class RangeHolder(
        val low: Double,
        val middle: Double,
        val high: Double,
        val isIncomplete: Boolean = true,
        val isFinalBin: Boolean = false
)

private val List<BinMember<Double>>.asDoubleBin: VariableDualMemberWidthBin<Double>
    get() = VariableDualMemberWidthBin(
            startValue = this.map { it.value }.min() ?: 0.0,
            endValue = this.map { it.value }.max() ?: 0.0,
            members = this.sortedBy { it.value }
    )