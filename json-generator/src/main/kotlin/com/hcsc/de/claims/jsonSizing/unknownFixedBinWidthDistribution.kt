package com.hcsc.de.claims.jsonSizing

import com.hcsc.de.claims.helpers.averageInt
import com.hcsc.de.claims.helpers.ceiling
import com.hcsc.de.claims.helpers.medianInt
import com.hcsc.de.claims.helpers.modeInt
import org.apache.commons.math3.util.IntegerSequence

interface BinDistribution<out numberType: Number> : Distribution<numberType> {
    val bins: List<Bin>
}

interface Bin {
    val count: Int
}

interface UnknownFixedBinWidthDistribution<out numberType : Number> : BinDistribution<numberType> {
    val numberOfBins: Int
    val sizeOfBin: Int
    override val bins: List<FixedWidthBin<numberType>>
}

data class FixedWidthBin<out numberType : Number>(
        val startValue: numberType,
        override val count: Int
) : Bin

data class UnknownIntFixedBinWidthDistribution(
        override val average: Int,
        override val minimum: Int,
        override val numberOfBins: Int,
        override val maximum: Int,
        override val sizeOfBin: Int,
        override val mode: Int,
        override val bins: List<FixedWidthBin<Int>>,
        override val median: Int
) : UnknownFixedBinWidthDistribution<Int> {

    override fun random(): Int {
        TODO("not implemented")
    }
}

fun List<Int>.unknownDistribution(
        numberOfBins: Int = 5,
        rangeMinimum: Int? = null,
        rangeMaximum: Int? = null
): UnknownFixedBinWidthDistribution<Int> {

    val sorted = this.sorted()

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

    val range = actualMaximum - actualMinimum

    val preliminarySizeOfBin = (range.toDouble() / numberOfBins).ceiling().toInt()

    val sizeOfBin = if (preliminarySizeOfBin == 0) 1 else preliminarySizeOfBin

    val initialBins =  IntegerSequence.Range(actualMinimum, actualMaximum, sizeOfBin).toList().map {
        FixedWidthBin(startValue = it, count = 0)
    }

    val bins = sortedAndFilteredByMinimumAndMaximum.fold(initialBins) { bins, int ->

        val binNumber = (int - actualMinimum) / sizeOfBin

        val startValue = binNumber * sizeOfBin + actualMinimum

        var binExist = false

        val binCopy = bins.map {
            if (it.startValue == startValue) {
                binExist = true
                it.copy(count = it.count + 1)
            } else {
                it
            }
        }

        if (binExist) {
            binCopy
        } else {
            bins.plus(FixedWidthBin(startValue = startValue, count = 1))
        }
    }.sortedBy { it.startValue }

    return UnknownIntFixedBinWidthDistribution(
            average = sorted.averageInt(),
            minimum = observedMinimum,
            maximum = observedMaximum,
            mode = sorted.modeInt(),
            median = sorted.medianInt(),
            numberOfBins = bins.count(),
            sizeOfBin = sizeOfBin,
            bins = bins.map { FixedWidthBin(startValue = it.startValue, count = it.count) }
    )
}