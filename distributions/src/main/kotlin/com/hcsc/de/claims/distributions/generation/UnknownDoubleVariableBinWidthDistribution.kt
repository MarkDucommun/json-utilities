package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.distributions.binDistributions.BinsWithMembersDistribution
import com.hcsc.de.claims.distributions.bins.BinWithMembers
import com.hcsc.de.claims.distributions.bins.DoubleBinWithMembers
import com.hcsc.de.claims.math.helpers.median
import com.hcsc.de.claims.math.helpers.mode


fun List<Double>.variableBinWidthDistribution(
        binCount: Int = 5,
        rangeMinimum: Double? = null,
        rangeMaximum: Double? = null
): BinsWithMembersDistribution<Double> {

    val sorted: List<Double> = this.sorted()

    val sortedAndFilteredByMinimum = rangeMinimum
            ?.let { sorted.filterNot { it < rangeMinimum } }
            ?: sorted

    val sortedAndFilteredByMinimumAndMaximum = rangeMaximum
            ?.let { sortedAndFilteredByMinimum.filterNot { it > rangeMaximum } }
            ?: sortedAndFilteredByMinimum

    val observedMinimum = sortedAndFilteredByMinimumAndMaximum.min() ?: 0.0
    val observedMaximum = sortedAndFilteredByMinimumAndMaximum.max() ?: 0.0

    return BinsWithMembersDistribution(
            average = sortedAndFilteredByMinimumAndMaximum.average(),
            minimum = observedMinimum,
            maximum = observedMaximum,
            median = sortedAndFilteredByMinimumAndMaximum.median(),
            mode = sortedAndFilteredByMinimumAndMaximum.mode(),
            bins = sortedAndFilteredByMinimumAndMaximum.asDoubleBin.maximizeBins(binCount)
    )
}

private fun BinWithMembers<Double>.maximizeBins(binCount: Int): List<BinWithMembers<Double>> {

    val median = members.average()

    val lower = members.filterNot { it >= median }

    val upper = members.filter { it >= median }

    return if (endValue - startValue > 0 && lower.count() > 5 && upper.count() > binCount) {

        listOf(lower.asDoubleBin.maximizeBins(binCount), upper.asDoubleBin.maximizeBins(binCount)).flatten()
    } else {
        listOf(this)
    }
}

private val List<Double>.asDoubleBin: BinWithMembers<Double>
    get() = DoubleBinWithMembers(members = this)