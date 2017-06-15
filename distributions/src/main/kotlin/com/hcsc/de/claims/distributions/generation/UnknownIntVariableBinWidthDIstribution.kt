package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.distributions.binDistributions.BinsWithMembersDistribution
import com.hcsc.de.claims.distributions.bins.BinWithMembers
import com.hcsc.de.claims.distributions.bins.IntBinWithMembers
import com.hcsc.de.claims.math.helpers.averageInt
import com.hcsc.de.claims.math.helpers.medianInt
import com.hcsc.de.claims.math.helpers.modeInt

fun List<Int>.variableBinWidthDistribution(
        binCount: Int = 5,
        rangeMinimum: Int? = null,
        rangeMaximum: Int? = null
): BinsWithMembersDistribution<Int> {

    val sorted: List<Int> = this.sorted()

    val sortedAndFilteredByMinimum = rangeMinimum
            ?.let { sorted.filterNot { it < rangeMinimum } }
            ?: sorted

    val sortedAndFilteredByMinimumAndMaximum = rangeMaximum
            ?.let { sortedAndFilteredByMinimum.filterNot { it > rangeMaximum } }
            ?: sortedAndFilteredByMinimum

    val observedMinimum = sortedAndFilteredByMinimumAndMaximum.min() ?: 0
    val observedMaximum = sortedAndFilteredByMinimumAndMaximum.max() ?: 0

    return BinsWithMembersDistribution(
            average = sortedAndFilteredByMinimumAndMaximum.averageInt(),
            minimum = observedMinimum,
            maximum = observedMaximum,
            median = sortedAndFilteredByMinimumAndMaximum.medianInt(),
            mode = sortedAndFilteredByMinimumAndMaximum.modeInt(),
            bins = sortedAndFilteredByMinimumAndMaximum.asBin.maximizeBins(binCount)
    )
}

private fun BinWithMembers<Int>.maximizeBins(binCount: Int): List<BinWithMembers<Int>> {

    val median = members.averageInt()

    val lower = members.filterNot { it >= median }

    val upper = members.filter { it >= median }

    return if (endValue - startValue > 0 && lower.count() > 5 && upper.count() > binCount) {

        listOf(lower.asBin.maximizeBins(binCount), upper.asBin.maximizeBins(binCount)).flatten()
    } else {
        listOf(this)
    }
}

private val List<Int>.asBin: BinWithMembers<Int>
    get() = IntBinWithMembers(members = this.sorted())