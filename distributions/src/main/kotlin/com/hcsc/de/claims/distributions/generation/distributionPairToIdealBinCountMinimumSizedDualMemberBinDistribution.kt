@file:JvmName("UnknownDualMemberVariableBinWidthDistributionKt")

package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.collection.helpers.NonEmptyList
import com.hcsc.de.claims.collection.helpers.asNonEmptyList
import com.hcsc.de.claims.collection.helpers.filterNotGreaterThan
import com.hcsc.de.claims.collection.helpers.filterNotLessThan
import com.hcsc.de.claims.distributions.DistributionPair
import com.hcsc.de.claims.distributions.NonEmptyDistributionPair
import com.hcsc.de.claims.distributions.SplitHolder
import com.hcsc.de.claims.distributions.binDistributions.AutomaticDualMemberBinsDistribution
import com.hcsc.de.claims.distributions.binDistributions.DualMemberBinsDistribution
import com.hcsc.de.claims.distributions.bins.*
import com.hcsc.de.claims.math.helpers.ceiling
import com.hcsc.de.claims.results.*

fun <numberType : Number> DistributionPair<numberType>.idealBinCountMinimumSizedDualMemberBinDistribution(
        minimumBinSize: Int = 5,
        rangeMinimum: numberType? = null,
        rangeMaximum: numberType? = null,
        toType: Double.() -> numberType
): Result<String, DualMemberBinsDistribution<numberType>> {

    val (listOne, listTwo) = this

    fun List<numberType>.transformList() = transformList(min = rangeMinimum, max = rangeMaximum, toType = toType)

    return zip(listOne.transformList(), listTwo.transformList()).map {

        NonEmptyDistributionPair(it.first, it.second).idealBinCountMinimumSizedDualMemberBinDistribution(
                minimumBinSize = minimumBinSize,
                toType = toType
        )
    }
}

fun <numberType : Number> NonEmptyDistributionPair<numberType>.idealBinCountMinimumSizedDualMemberBinDistribution(
        minimumBinSize: Int = 5,
        toType: Double.() -> numberType
): DualMemberBinsDistribution<numberType> {

    val (listOne, listTwo) = this

    val idealBinCount = Math.floor(Math.pow(1.88 * (listOne.size), (2.0 / 5.0))).toInt()

    val dualMemberBin = DoubleDualMemberBin(
            DoubleBinWithMembers(members = listOne.map(Number::toDouble)),
            DoubleBinWithMembers(members = listTwo.map(Number::toDouble))
    )

    val bins = List(idealBinCount - 1) { it }.fold(BinHolder(bins = listOf(dualMemberBin))) { binHolder, _ ->

        if (binHolder.isComplete) {
            binHolder
        } else {

            val sortedBins = binHolder.bins.sortedBy { it.width }

            val splittableSortedBins = sortedBins.filter { it.binOne.size / 2 >= minimumBinSize && it.binTwo.size / 2 >= minimumBinSize }

            if (splittableSortedBins.isNotEmpty()) {

                val splitBins = splittableSortedBins.reversed().fold(SplitHolder()) { acc, bin ->

                    acc.notComplete {
                        val bins: List<DualMemberBin<Double, BinWithMembers<Double>>> = bin.splitBin(minimumBinSize)

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
            rawBins = bins.map {
                AutomaticDualMemberBin(
                        binOne = AutomaticBinWithMembers(rawMembers = it.binOne.members.map(toType), toType = toType),
                        binTwo = AutomaticBinWithMembers(rawMembers = it.binTwo.members.map(toType), toType = toType),
                        toType = toType
                )
            },
            toType = toType
    )
}


private fun DualMemberBin<Double, BinWithMembers<Double>>.splitBin(binSize: Int): List<DualMemberBin<Double, BinWithMembers<Double>>> {

    val (low, high) = if (binOne.average < binTwo.average) binOne.average to binTwo.average else binTwo.average to binOne.average

    val rangeHolder = RangeHolder(low = low, middle = (low + high) / 2, high = high, bins = listOf(this))

    return List(Math.log(low + high).ceiling().toInt()) { it }.fold(rangeHolder) { rangeHolder, _ ->

        if (rangeHolder.isIncomplete) {

            val splitOneResult = binOne.split(rangeHolder.middle).flatMap { it.isValid(binSize) }
            val splitTwoResult = binTwo.split(rangeHolder.middle).flatMap { it.isValid(binSize) }

            if (splitOneResult is Success && splitTwoResult is Success) {

                val splitOne = splitOneResult.content
                val splitTwo = splitTwoResult.content

                rangeHolder.copy(
                        isFinalBin = false,
                        isIncomplete = false,
                        bins = listOf(
                                DoubleDualMemberBin(
                                        binOne = splitOne.upper,
                                        binTwo = splitTwo.upper
                                ),
                                DoubleDualMemberBin(
                                        binOne = splitOne.lower,
                                        binTwo = splitTwo.lower
                                )
                        ))

            } else if (splitOneResult is Success) {

                rangeHolder.copy(high = rangeHolder.middle, middle = (rangeHolder.middle + rangeHolder.low) / 2)

            } else if (splitTwoResult is Success) {

                rangeHolder.copy(low = rangeHolder.middle, middle = (rangeHolder.high + rangeHolder.middle) / 2)

            } else {

                rangeHolder.copy(isFinalBin = true, isIncomplete = false)
            }
        } else {
            rangeHolder
        }
    }.bins
}

fun <numberType : Number> List<numberType>.transformList(
        min: numberType?,
        max: numberType?,
        toType: Double.() -> numberType): Result<String, NonEmptyList<numberType>> =
        this
                .map(Number::toDouble)
                .sorted()
                .filterNotLessThan(min?.toDouble())
                .filterNotGreaterThan(max?.toDouble())
                .map(toType)
                .asNonEmptyList()

private val <numberType : Number> DualMemberBin<numberType, BinWithMembers<numberType>>.smallestMemberCount: Int
    get() = if (binOne.size > binTwo.size) binTwo.size else binOne.size

private fun <numberType : Number> DualMemberBin<numberType, BinWithMembers<numberType>>.isValid(count: Int): Boolean =
        binOne.size >= count && binTwo.size >= count

private fun <numberType : Number> SplitBinHolder<numberType, BinWithMembers<numberType>>.isValid(binSize: Int): Result<String, SplitBinHolder<numberType, BinWithMembers<numberType>>> {

    return if (lower.size >= binSize && upper.size >= binSize) {
        Success(this)
    } else {
        Failure("")
    }
}