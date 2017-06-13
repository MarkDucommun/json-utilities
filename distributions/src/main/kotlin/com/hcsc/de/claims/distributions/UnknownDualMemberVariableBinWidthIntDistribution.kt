@file:JvmName("UnknownDualMemberVariableBinWidthDistributionKt")

package com.hcsc.de.claims.distributions

import com.hcsc.de.claims.math.helpers.ceiling
import com.hcsc.de.claims.math.helpers.ceilingOnEven
import com.hcsc.de.claims.math.helpers.median
import com.hcsc.de.claims.math.helpers.mode

data class DistributionPair<out numberType : Number>(
        val one: List<numberType>,
        val two: List<numberType>
)

fun <numberType : Number> DistributionPair<numberType>.unknownDualMemberVariableBinWidthDistribution(
        minimumBinMemberCount: Int = 5,
        rangeMinimum: numberType? = null,
        rangeMaximum: numberType? = null
): UnknownDualMemberVariableBinWidthDistribution<Double> {

    val (listOne, listTwo) = this

    val sortedOne = listOne.map(Number::toDouble).sorted().map(Double::ceilingOnEven)
    val sortedTwo = listTwo.map(Number::toDouble).sorted().map(Double::ceilingOnEven)

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

    val idealBinCount = Math.floor(Math.pow(1.88 * (listOne.size), (2.0 / 5.0))).toInt()

    val bins = List(idealBinCount - 1) { it }.fold(BinHolder(bins = listOf(sortedAndFilteredByMinimumAndMaximum.asDoubleBin))) { binHolder, _ ->

        if (binHolder.isComplete) {
            binHolder
        } else {

            val sortedBins = binHolder.bins.sortedBy { it.range }

            val splittableSortedBins = sortedBins.filter { it.isSplittable(minimumBinMemberCount) }

            if (splittableSortedBins.isNotEmpty()) {

                val splitBins = splittableSortedBins.reversed().fold(SplitHolder()) { acc, bin ->

                    acc.notComplete {
                        val bins = bin.splitBin(minimumBinMemberCount)

                        if (bins.size == 2) {
                            SplitHolder(binSplit = bin, newBins = bins)
                        } else {
                            acc
                        }
                    }
                }

                if (splitBins.binSplit != null) {

                    val newBins = sortedBins.filterNot { splitBins.binSplit.identifyingCharacteristic == it.identifyingCharacteristic }.plus(splitBins.newBins)

                    binHolder.copy(bins = newBins)
                } else {
                    binHolder
                }

            } else {
                binHolder.copy(isComplete = true)
            }
        }
    }.bins

    return UnknownDualMemberVariableBinWidthDistribution(
            average = sortedAndFilteredByMinimumAndMaximum.map { it.value }.average(),
            minimum = observedMinimum,
            maximum = observedMaximum,
            median = sortedAndFilteredByMinimumAndMaximum.map { it.value }.median(),
            mode = sortedAndFilteredByMinimumAndMaximum.map { it.value }.mode(),
            bins = bins
    )
}

val <numberType : Number> UnknownDualMemberVariableBinWidthDistribution<numberType>.asTwoDistributions: Pair<UnknownVariableBinWidthDistribution<Double>, UnknownVariableBinWidthDistribution<Double>> get() {

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

private fun VariableDualMemberWidthBin<Double>.maximizeDoubleBinsRecursively(binCount: Int): List<VariableDualMemberWidthBin<Double>> {

    return if (isSplittable(binCount)) {

        val rangeHolder = RangeHolder(low = startValue, middle = (endValue + startValue) / 2, high = endValue)

        val finalRangeHolder = List(5) { it }.fold(rangeHolder) { rangeHolder, _ ->

            if (rangeHolder.isIncomplete) {

                val lower = members.filterNot { it.value >= rangeHolder.middle }.asDoubleBin

                val upper = members.filter { it.value >= rangeHolder.middle }.asDoubleBin

                if (lower.isValid(binCount) && upper.isValid(binCount)) {
                    rangeHolder.copy(isFinalBin = false, isIncomplete = false)
                } else if (lower.isValid(binCount)) {
                    rangeHolder.copy(high = rangeHolder.middle, middle = (rangeHolder.middle + rangeHolder.low) / 2)
                } else if (upper.isValid(binCount)) {
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

            val lowerBin = members.filterNot { it.value >= rangeHolder.middle }.asDoubleBin
            val upperBin = members.filter { it.value >= rangeHolder.middle }.asDoubleBin

            if (lowerBin.isValid(binCount) && upperBin.isValid(binCount)) {

                val lower = lowerBin.maximizeDoubleBinsRecursively(binCount)
                val upper = upperBin.maximizeDoubleBinsRecursively(binCount)

                lower.plus(upper)
            } else {
                listOf(this)
            }

        }
    } else {

        listOf(this)
    }
}

private fun VariableDualMemberWidthBin<Double>.splitBin(binCount: Int): List<VariableDualMemberWidthBin<Double>> {

    return if (isSplittable(binCount)) {

        val memberOneAverage = memberOneValues.average()

        val memberTwoAverage = memberTwoValues.average()

        val (low, high) = if (memberOneAverage < memberTwoAverage) memberOneAverage to memberTwoAverage else memberTwoAverage to memberOneAverage

        val rangeHolder = RangeHolder(low = low, middle = (low + high) / 2, high = high)

        val finalRangeHolder = List(Math.log(low + high).ceiling().toInt()) { it }.fold(rangeHolder) { rangeHolder, _ ->

            if (rangeHolder.isIncomplete) {

                val lower = members.filterNot { it.value >= rangeHolder.middle }.asDoubleBin

                val upper = members.filter { it.value >= rangeHolder.middle }.asDoubleBin

                if (lower.isValid(binCount) && upper.isValid(binCount)) {
                    rangeHolder.copy(isFinalBin = false, isIncomplete = false)
                } else if (lower.isValid(binCount)) {
                    rangeHolder.copy(high = rangeHolder.middle, middle = (rangeHolder.middle + rangeHolder.low) / 2)
                } else if (upper.isValid(binCount)) {
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

            val lowerBin = members.filterNot { it.value >= rangeHolder.middle }.asDoubleBin
            val upperBin = members.filter { it.value >= rangeHolder.middle }.asDoubleBin

            if (lowerBin.isValid(binCount) && upperBin.isValid(binCount)) {

                listOf(lowerBin, upperBin)
            } else {
                listOf(this)
            }

        }
    } else {

        listOf(this)
    }
}

private val VariableDualMemberWidthBin<Double>.smallestMemberCount: Int get() {
    return if (memberOneCount > memberTwoCount) memberTwoCount else memberOneCount
}

private fun <numberType : Number> VariableDualMemberWidthBin<numberType>.isValid(count: Int): Boolean {
    return memberOneCount >= count && memberTwoCount >= count
}

private fun <numberType : Number> VariableDualMemberWidthBin<numberType>.isSplittable(count: Int): Boolean {

    val memberOneSet = memberOneValues.toSet()
    val memberTwoSet = memberTwoValues.toSet()



    return endValue.toDouble() - startValue.toDouble() > 0.0
            && memberOneCount >= count * 2 && memberTwoCount >= count * 2
            && memberOneSet.size > 1 && memberTwoSet.size > 1
            && memberOneValues.filter { it < median }.size >= count
            && memberOneValues.filterNot { it < median }.size >= count
            && memberTwoValues.filter{ it < median }.size >= count
            && memberTwoValues.filterNot { it < median }.size >= count
            && memberOneValues.filter { it < average }.size >= count
            && memberOneValues.filterNot { it < average }.size >= count
            && memberTwoValues.filter{ it < average }.size >= count
            && memberTwoValues.filterNot { it < average }.size >= count
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

data class SplitHolder(
        val binSplit: VariableDualMemberWidthBin<Double>? = null,
        val newBins: List<VariableDualMemberWidthBin<Double>> = emptyList()
){
    fun notComplete(fn: () -> SplitHolder): SplitHolder {
        return if (binSplit == null) {
            fn.invoke()
        } else {
            this
        }
    }
}

data class BinHolder(
        val isComplete: Boolean = false,
        val bins: List<VariableDualMemberWidthBin<Double>>
)