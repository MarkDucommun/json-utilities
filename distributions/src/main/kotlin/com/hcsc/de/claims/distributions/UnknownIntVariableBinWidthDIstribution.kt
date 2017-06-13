package com.hcsc.de.claims.distributions

import com.hcsc.de.claims.math.helpers.averageInt
import com.hcsc.de.claims.math.helpers.medianInt
import com.hcsc.de.claims.math.helpers.modeInt

fun List<Int>.unknownVariableBinWidthDistribution(
        binCount: Int = 5,
        rangeMinimum: Int? = null,
        rangeMaximum: Int? = null
): UnknownVariableBinWidthDistribution<Int> {

    val sorted: List<Int> = this.sorted()

    val sortedAndFilteredByMinimum = rangeMinimum
            ?.let { sorted.filterNot { it < rangeMinimum } }
            ?: sorted

    val sortedAndFilteredByMinimumAndMaximum = rangeMaximum
            ?.let { sortedAndFilteredByMinimum.filterNot { it > rangeMaximum } }
            ?: sortedAndFilteredByMinimum

    val observedMinimum = sortedAndFilteredByMinimumAndMaximum.min() ?: 0
    val observedMaximum = sortedAndFilteredByMinimumAndMaximum.max() ?: 0

    return UnknownVariableBinWidthDistribution(
            average = sortedAndFilteredByMinimumAndMaximum.averageInt(),
            minimum = observedMinimum,
            maximum = observedMaximum,
            median = sortedAndFilteredByMinimumAndMaximum.medianInt(),
            mode = sortedAndFilteredByMinimumAndMaximum.modeInt(),
            bins = sortedAndFilteredByMinimumAndMaximum.asBin.maximizeBins(binCount)
    )
}

private fun VariableWidthBin<Int>.maximizeBins(binCount: Int): List<VariableWidthBin<Int>> {

    val median = members.averageInt()

    val lower = members.filterNot { it >= median }

    val upper = members.filter { it >= median }

    return if (endValue - startValue > 0 && lower.count() > 5 && upper.count() > binCount) {

        listOf(lower.asBin.maximizeBins(binCount), upper.asBin.maximizeBins(binCount)).flatten()
    } else {
        listOf(this)
    }
}

private val List<Int>.asBin: VariableWidthBin<Int>
    get() = VariableWidthBin(
            startValue = this.min() ?: 0,
            endValue = this.max() ?: 0,
            members = this.sorted()
    )