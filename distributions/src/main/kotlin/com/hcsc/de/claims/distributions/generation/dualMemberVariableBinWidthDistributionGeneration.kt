@file:JvmName("UnknownDualMemberVariableBinWidthDistributionKt")

package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.distributions.DistributionPair
import com.hcsc.de.claims.distributions.SplitHolder
import com.hcsc.de.claims.distributions.binDistributions.AutomaticDualMemberBinsDistribution
import com.hcsc.de.claims.distributions.binDistributions.DualMemberBinsDistribution
import com.hcsc.de.claims.distributions.bins.BinWithMembers
import com.hcsc.de.claims.distributions.bins.DoubleBinWithMembers
import com.hcsc.de.claims.distributions.bins.DoubleDualMemberBin
import com.hcsc.de.claims.distributions.bins.DualMemberBin
import com.hcsc.de.claims.math.helpers.ceiling
import com.hcsc.de.claims.math.helpers.ceilingOnEven

fun <numberType : Number> DistributionPair<numberType>.unknownDualMemberVariableBinWidthDistribution(
        minimumBinMemberCount: Int = 5,
        rangeMinimum: numberType? = null,
        rangeMaximum: numberType? = null
): DualMemberBinsDistribution<Double> {

    val (listOne, listTwo) = this

    val sortedOne = listOne.map(Number::toDouble).sorted().map(Double::ceilingOnEven)
    val sortedTwo = listTwo.map(Number::toDouble).sorted().map(Double::ceilingOnEven)

    val sortedAndFilteredByMinimumOne = rangeMinimum
            ?.let { sortedOne.filterNot { it < rangeMinimum.toDouble() } }
            ?: sortedOne

    val sortedAndFilteredByMinimumTwo = rangeMinimum
            ?.let { sortedTwo.filterNot { it < rangeMinimum.toDouble() } }
            ?: sortedOne

    val sortedAndFilteredByMinimumAndMaximumOne = rangeMaximum
            ?.let { sortedAndFilteredByMinimumOne.filterNot { it > rangeMaximum.toDouble() } }
            ?: sortedAndFilteredByMinimumOne

    val sortedAndFilteredByMinimumAndMaximumTwo = rangeMaximum
            ?.let { sortedAndFilteredByMinimumTwo.filterNot { it > rangeMaximum.toDouble() } }
            ?: sortedAndFilteredByMinimumTwo

    val idealBinCount = Math.floor(Math.pow(1.88 * (listOne.size), (2.0 / 5.0))).toInt()

    val dualMemberBin = DoubleDualMemberBin(
            DoubleBinWithMembers(members = sortedAndFilteredByMinimumAndMaximumOne),
            DoubleBinWithMembers(members = sortedAndFilteredByMinimumAndMaximumTwo)
    )

    val bins = List(idealBinCount - 1) { it }.fold(BinHolder(bins = listOf(dualMemberBin))) { binHolder, _ ->

        if (binHolder.isComplete) {
            binHolder
        } else {

            val sortedBins = binHolder.bins.sortedBy { it.width }

            val splittableSortedBins = sortedBins.filter { it.isSplittable(minimumBinMemberCount) }

            if (splittableSortedBins.isNotEmpty()) {

                val splitBins = splittableSortedBins.reversed().fold(SplitHolder()) { acc, bin ->

                    acc.notComplete {
                        val bins: List<DualMemberBin<Double, BinWithMembers<Double>>> = bin.splitBin(minimumBinMemberCount)

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

    return AutomaticDualMemberBinsDistribution(
            rawBins = bins,
            toType = Double::toDouble
    )
}

private fun <numberType : Number> DualMemberBin<numberType, BinWithMembers<numberType>>.splitBin(binCount: Int): List<DualMemberBin<numberType, BinWithMembers<numberType>>> {

    return if (isSplittable(binCount)) {

        val memberOneAverage = binOne.average.toDouble()

        val memberTwoAverage = binTwo.average.toDouble()

        val (low, high) = if (memberOneAverage < memberTwoAverage) memberOneAverage to memberTwoAverage else memberTwoAverage to memberOneAverage

        val rangeHolder = RangeHolder(low = low, middle = (low + high) / 2, high = high)

        val finalRangeHolder = List(Math.log(low + high).ceiling().toInt()) { it }.fold(rangeHolder) { rangeHolder, _ ->

            if (rangeHolder.isIncomplete) {

                val (upper, lower) = splitByDouble(rangeHolder.middle)

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

            val (upperBin, lowerBin) = this.splitByDouble(rangeHolder.middle)

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

private val <numberType : Number> DualMemberBin<numberType, BinWithMembers<numberType>>.smallestMemberCount: Int
    get() = if (binOne.size > binTwo.size) binTwo.size else binOne.size

private fun <numberType : Number> DualMemberBin<numberType, BinWithMembers<numberType>>.isValid(count: Int): Boolean =
        binOne.size >= count && binTwo.size >= count

private fun <numberType : Number> DualMemberBin<numberType, BinWithMembers<numberType>>.isSplittable(count: Int): Boolean {

    val memberOneSet = binOne.members.toSet()
    val memberTwoSet = binTwo.members.toSet()

    val binWidthIsNotZero = endValue.toDouble() - startValue.toDouble() > 0.0

    return binWidthIsNotZero
            && binOne.size >= count * 2 && binTwo.size >= count * 2
            && memberOneSet.size > 1 && memberTwoSet.size > 1
            && binOne.doubleMembers.filter { it < median.toDouble() }.size >= count
            && binOne.doubleMembers.filterNot { it < median.toDouble() }.size >= count
            && binTwo.doubleMembers.filter { it < median.toDouble() }.size >= count
            && binTwo.doubleMembers.filterNot { it < median.toDouble() }.size >= count
            && binOne.doubleMembers.filter { it < average.toDouble() }.size >= count
            && binOne.doubleMembers.filterNot { it < average.toDouble() }.size >= count
            && binTwo.doubleMembers.filter { it < average.toDouble() }.size >= count
            && binTwo.doubleMembers.filterNot { it < average.toDouble() }.size >= count
}
