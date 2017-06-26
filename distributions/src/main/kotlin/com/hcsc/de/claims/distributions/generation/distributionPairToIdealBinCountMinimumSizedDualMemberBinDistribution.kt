@file:JvmName("UnknownDualMemberVariableBinWidthDistributionKt")

package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.collection.helpers.*
import com.hcsc.de.claims.distributions.DistributionPair
import com.hcsc.de.claims.distributions.NonEmptyDistributionPair
import com.hcsc.de.claims.distributions.SplitHolder
import com.hcsc.de.claims.distributions.binDistributions.AutomaticDualMemberBinsDistribution
import com.hcsc.de.claims.distributions.binDistributions.DualMemberBinsDistribution
import com.hcsc.de.claims.distributions.bins.*
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
        binCount: Int? = null,
        toType: Double.() -> numberType
): DualMemberBinsDistribution<numberType> {

    val (listOne, listTwo) = this

    val idealBinCount = binCount ?: if (listOne.size > 35) {
        Math.floor(Math.pow(1.88 * (listOne.size), (2.0 / 5.0))).toInt()
    } else {
        Math.round(listOne.size / 5.0).toInt()
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

    val bins = rangeHolder.whileTrue(continueFn = { it.isComplete.not() && Math.abs(it.low - it.high) > 0.0001 }) {

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
                    is SingleSourceSplitFailure -> {
                        val content = result.content as SourceOneSplitFailureWithValue

                        if (content.failure is UpperSplitFailure) {
                            rangeHolder.shiftDown()
                        } else if (content.failure is LowerSplitFailure) {
                            rangeHolder.shiftUp()
                        } else {
                            rangeHolder.complete()
                        }
                    }
                    is BothSourceSplitFailureWithValues -> {
                        val content = result.content as BothSourceSplitFailureWithValues

                        if (content.sourceOneFailure is UpperSplitFailure && content.sourceTwoFailure is UpperSplitFailure) {
                            rangeHolder.shiftDown()
                        } else if (content.sourceOneFailure is LowerSplitFailure && content.sourceTwoFailure is LowerSplitFailure) {
                            rangeHolder.shiftUp()
                        } else {
                            rangeHolder.complete()
                        }
                    }
                }
            }
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

tailrec fun <accumulator> accumulator.whileTrue(iteration: Int = 0, maxIterations: Int = 1000, continueFn: (accumulator) -> Boolean, operation: (accumulator) -> accumulator): accumulator {
    return if (iteration < maxIterations && continueFn(this)) {
        operation(this).whileTrue(iteration + 1, maxIterations, continueFn, operation)
    } else {
        this
    }
}

fun <type> Int.loopWithAccumulator(initial: type, operation: (type) -> type): type {
    return List(this) { it }.fold(initial) { acc, _ -> operation(acc) }
}

fun RangeHolder.shiftUp(): RangeHolder { return copy(low = middle, middle = (high + middle) / 2) }
fun RangeHolder.shiftDown(): RangeHolder { return copy(high = middle, middle = (low + middle) / 2) }
fun RangeHolder.complete(): RangeHolder { return copy(isComplete = true) }

