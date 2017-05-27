package com.hcsc.de.claims.jsonSizing

import com.hcsc.de.claims.helpers.averageInt
import com.hcsc.de.claims.helpers.medianInt
import com.hcsc.de.claims.helpers.modeInt
import java.util.*

interface UnknownVariableBinWidthDistribution<out numberType : Number> : BinDistribution<numberType> {
    val numberOfBins: Int
    override val bins: List<VariableWidthBin<numberType>>
}

data class VariableWidthBin<out numberType : Number>(
        val startValue: numberType,
        val endValue: numberType,
        val members: List<numberType>
) : Bin {
    override val count: Int = members.size
    val width: Int = endValue.toInt() - startValue.toInt()
}

data class UnknownIntVariableBinWidthDistribution(
        override val average: Int,
        override val minimum: Int,
        override val maximum: Int,
        override val median: Int,
        override val mode: Int,
        override val bins: List<VariableWidthBin<Int>>
) : UnknownVariableBinWidthDistribution<Int> {

    private val random = Random()

    override fun random(): Int {

        val index = random.nextInt(bins.size)

        val bin = bins[index]

//        return bin.members[random.nextInt(bin.members.size)]
        return bin.startValue
    }

    override val numberOfBins: Int = bins.size
}

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

    val actualMinimum = rangeMinimum ?: observedMinimum
    val actualMaximum = rangeMaximum ?: observedMaximum

    return UnknownIntVariableBinWidthDistribution(
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