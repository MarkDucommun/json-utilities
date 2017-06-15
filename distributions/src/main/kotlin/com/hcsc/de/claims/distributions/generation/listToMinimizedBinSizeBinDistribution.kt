package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.collection.helpers.filterNotGreaterThan
import com.hcsc.de.claims.collection.helpers.filterNotLessThan
import com.hcsc.de.claims.distributions.binDistributions.BinDistribution
import com.hcsc.de.claims.distributions.binDistributions.DoubleBinWithMembersDistribution
import com.hcsc.de.claims.distributions.binDistributions.IntBinWithMembersDistribution
import com.hcsc.de.claims.distributions.bins.BinWithMembers
import com.hcsc.de.claims.distributions.bins.DoubleBinWithMembers
import com.hcsc.de.claims.distributions.bins.IntBinWithMembers


fun List<Double>.minimizedBinSizeBinDistribution(
        minimumBinSize: Int = 5,
        rangeMinimum: Double? = null,
        rangeMaximum: Double? = null
): BinDistribution<Double, BinWithMembers<Double>> =
        genericMinimizedBinSizeBinDistribution(
                minimumBinSize = minimumBinSize,
                rangeMinimum = rangeMinimum,
                rangeMaximum = rangeMaximum,
                toBinNumberType = { this },
                toBinDistribution = { DoubleBinWithMembersDistribution(bins = this) }
        )

fun List<Int>.minimizedBinSizeBinDistribution(
        minimumBinSize: Int = 5,
        rangeMinimum: Int? = null,
        rangeMaximum: Int? = null
): BinDistribution<Int, BinWithMembers<Int>> =
        genericMinimizedBinSizeBinDistribution(
                minimumBinSize = minimumBinSize,
                rangeMinimum = rangeMinimum,
                rangeMaximum = rangeMaximum,
                toBinNumberType = { IntBinWithMembers(members = this.members.map(Double::toInt)) },
                toBinDistribution = { IntBinWithMembersDistribution(bins = this) }
        )

fun <numberType : Number> List<numberType>.genericMinimizedBinSizeBinDistribution(
        minimumBinSize: Int = 5,
        rangeMinimum: numberType? = null,
        rangeMaximum: numberType? = null,
        toBinNumberType: BinWithMembers<Double>.() -> BinWithMembers<numberType>,
        toBinDistribution: List<BinWithMembers<numberType>>.() -> BinDistribution<numberType, BinWithMembers<numberType>>
): BinDistribution<numberType, BinWithMembers<numberType>> =
        this
                .map(Number::toDouble)
                .sorted()
                .filterNotLessThan(rangeMinimum?.toDouble())
                .filterNotGreaterThan(rangeMaximum?.toDouble())
                .asDoubleBin
                .maximizeBins(minimumBinSize)
                .map(toBinNumberType)
                .toBinDistribution()

private fun BinWithMembers<Double>.maximizeBins(binCount: Int): List<BinWithMembers<Double>> =
        splitByDouble(members.average()).let { (upper, lower) ->
            if (lower.isValid(binCount) && upper.isValid(binCount)) {
                listOf(lower.maximizeBins(binCount), upper.maximizeBins(binCount)).flatten()
            } else {
                listOf(this)
            }
        }

private val List<Double>.asDoubleBin: BinWithMembers<Double> get() = DoubleBinWithMembers(members = this)

private fun BinWithMembers<Double>.isValid(binCount: Int): Boolean = endValue - startValue >= 0.0 && size >= binCount