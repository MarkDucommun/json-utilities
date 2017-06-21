@file:JvmName("UnknownDualMemberVariableBinWidthDistributionKt")

package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.collection.helpers.*
import com.hcsc.de.claims.distributions.DistributionPair
import com.hcsc.de.claims.distributions.NonEmptyDistributionPair
import com.hcsc.de.claims.distributions.SplitHolder
import com.hcsc.de.claims.distributions.binDistributions.AutomaticDualMemberBinsDistribution
import com.hcsc.de.claims.distributions.binDistributions.DualMemberBinsDistribution
import com.hcsc.de.claims.distributions.bins.*
import com.hcsc.de.claims.math.helpers.ceiling
import com.hcsc.de.claims.results.*

fun <numberType : Number> DistributionPair<numberType>.idealBinCountMinimumSizedDualSourceBinWithMembersDistribution(
        minimumSourceBinSize: Int = 5,
        rangeMinimum: numberType? = null,
        rangeMaximum: numberType? = null,
        toType: Double.() -> numberType
): Result<String, DualMemberBinsDistribution<numberType>> {

    val (listOne, listTwo) = this

    fun List<numberType>.transformList() = transformList(min = rangeMinimum, max = rangeMaximum, toType = toType)

    return zip(listOne.transformList(), listTwo.transformList()).map {

        NonEmptyDistributionPair(it.first, it.second).idealBinCountMinimumSizedDualSourceBinWithMembersDistribution(
                minimumSourceBinSize = minimumSourceBinSize,
                toType = toType
        )
    }
}

fun <numberType : Number> NonEmptyDistributionPair<numberType>.idealBinCountMinimumSizedDualSourceBinWithMembersDistribution(
        minimumSourceBinSize: Int = 5,
        toType: Double.() -> numberType
): DualMemberBinsDistribution<numberType> {

    val (listOne, listTwo) = this

    val idealBinCount = if (listOne.size > 35) {
        Math.floor(Math.pow(1.88 * (listOne.size), (2.0 / 5.0))).toInt()
    } else {
        Math.round(listOne.size / 5.0).toInt()
    }

    fun <type> Int.loopWithAccumulator(initial: type, operation: (type) -> type): type {
        return List(this) { it }.fold(initial) { acc, _ -> operation(acc) }
    }

    val initialBin = DoubleDualSourceBinWithMembers(
            DoubleBinWithMembers(members = listOne.map(Number::toDouble)),
            DoubleBinWithMembers(members = listTwo.map(Number::toDouble))
    )

    val bins = (idealBinCount - 1).loopWithAccumulator(initial = BinHolder(bins = listOf(initialBin))) { binHolder ->

        if (binHolder.isComplete) {

            binHolder

        } else {

            val sortedBins = binHolder.bins.sortedByDescending { it.width }

            // TODO make a result of a splitholder
            val splitBins = sortedBins.fold(SplitHolder()) { splitHolder, bin ->

                splitHolder.runIfNoBinWasSplitYet {

                    bin
                            .splitBin(minimumSourceBinSize = minimumSourceBinSize)
                            .map { SplitHolder(splitBin = bin, newBins = it) }
                            .getOrElse(splitHolder)
                }
            }

            if (splitBins.binWasSplit) {

                // TODO get rid of the double bangs, also make this into a remove function
                val newBins = sortedBins.filterNot { splitBins.splitBin!!.identifyingCharacteristic == it.identifyingCharacteristic }.plus(splitBins.newBins)

                binHolder.copy(bins = newBins)
            } else {
                binHolder.copy(isComplete = true)
            }
        }

    }.bins

    return AutomaticDualMemberBinsDistribution(
            rawBins = bins.sortedBy { it.startValue }.map {
                AutomaticDualSourceBinWithMembers(
                        sourceOneBin = AutomaticBinWithMembers(rawMembers = it.sourceOneBin.members.map(toType), toType = toType),
                        sourceTwoBin = AutomaticBinWithMembers(rawMembers = it.sourceTwoBin.members.map(toType), toType = toType),
                        toType = toType
                )
            },
            toType = toType
    )
}

private fun DualSourceBinWithMembers<Double, BinWithMembers<Double>>.splitBin(
        minimumSourceBinSize: Int
): Result<String, List<DualSourceBinWithMembers<Double, BinWithMembers<Double>>>> {

    val highestMinimum = Math.max(sourceOneBin.minimum, sourceTwoBin.minimum)
    val lowestMaximum = Math.min(sourceOneBin.maximum, sourceTwoBin.maximum)
    val splitPoint = (lowestMaximum + highestMinimum) / 2

    if (highestMinimum >= lowestMaximum) {
        return Failure("Bin not split")
    }

    val rangeHolder = RangeHolder(low = highestMinimum, middle = splitPoint, high = lowestMaximum, bins = listOf(this))

    val bins: List<DualSourceBinWithMembers<Double, BinWithMembers<Double>>> = List(10000) { it }.fold(rangeHolder) { rangeHolder, _ ->

        if (!rangeHolder.isComplete) {

            val result = AutomaticDualSourceBinWithMembers(
                    sourceOneBin = sourceOneBin,
                    sourceTwoBin = sourceTwoBin,
                    toType = doubleToDouble
            ).splitDualSourceBin(splitPoint = splitPoint, minimumSourceBinSize = minimumSourceBinSize)

            when (result) {
                is Success -> {
                    rangeHolder.copy(
                            isComplete = true,
                            bins = listOf(
                                    result.content.lower,
                                    result.content.upper
                            )
                    )

                }
                is Failure -> {
                    when (result.content) {
                        is SourceOneUpperSplitFailure, is SourceTwoUpperSplitFailure -> {
                            rangeHolder.copy(high = rangeHolder.middle, middle = (rangeHolder.middle + rangeHolder.low) / 2)
                        }
                        is SourceOneLowerSplitFailure -> TODO()
                        is SourceTwoLowerSplitFailure -> TODO()
                        is BothSourceUpperSplitFailure -> TODO()
                        is BothSourceLowerSplitFailure -> TODO()
                        is BothSourceUpperLowerSplitFailure, is BothSourceLowerUpperSplitFailure -> TODO()
                    }
                }
            }

//            val splitOneResult = sourceOneBin.split(rangeHolder.middle).mapError { "" }.flatMap { it.isValid(minimumBinSize) }
//            val splitTwoResult = sourceTwoBin.split(rangeHolder.middle).mapError { "" }.flatMap { it.isValid(minimumBinSize) }
//
//            if (splitOneResult is Success && splitTwoResult is Success) {
//
//                val (splitOneUpper, splitOneLower) = splitOneResult.content
//                val (splitTwoUpper, splitTwoLower) = splitTwoResult.content
//
//                rangeHolder.copy(
//                        isComplete = true,
//                        bins = listOf(
//                                DoubleDualSourceBinWithMembers(
//                                        sourceOneBin = splitOneUpper,
//                                        sourceTwoBin = splitTwoUpper
//                                ),
//                                DoubleDualSourceBinWithMembers(
//                                        sourceOneBin = splitOneLower,
//                                        sourceTwoBin = splitTwoLower
//                                )
//                        ))
//
//            } else if (splitOneResult is Success) {
//
//                rangeHolder.copy(high = rangeHolder.middle, middle = (rangeHolder.middle + rangeHolder.low) / 2)
//
//            } else if (splitTwoResult is Success) {
//
//                // TODO we are here, so are you. PS its broken
//
//                val filterHighResult = sourceOneBin.members.filter { it > rangeHolder.middle }
//
//                if (filterHighResult is Success && filterHighResult.content.size >= minimumBinSize) {
//
//                } else {
//
//                }
//
//                rangeHolder.copy(low = rangeHolder.middle, middle = (rangeHolder.high + rangeHolder.middle) / 2)
//
//            } else {
//                rangeHolder.copy(isComplete = true)
//            }
        } else {
            rangeHolder
        }
    }.bins


    return if (bins.size == 2) {
        Success(bins)
    } else {
        Failure("Bin not split")
    }

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

private val <numberType : Number> DualSourceBinWithMembers<numberType, BinWithMembers<numberType>>.smallestMemberCount: Int
    get() = if (sourceOneBin.size > sourceTwoBin.size) sourceTwoBin.size else sourceOneBin.size

private fun <numberType : Number> DualSourceBinWithMembers<numberType, BinWithMembers<numberType>>.isValid(count: Int): Boolean =
        sourceOneBin.size >= count && sourceTwoBin.size >= count

private fun <numberType : Number> SplitBinHolder<numberType, BinWithMembers<numberType>>.isValid(binSize: Int): Result<String, SplitBinHolder<numberType, BinWithMembers<numberType>>> {

    return if (lower.size >= binSize && upper.size >= binSize) {
        Success(this)
    } else {
        Failure("")
    }
}