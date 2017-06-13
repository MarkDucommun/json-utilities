package com.hcsc.de.claims.distributions

import com.hcsc.de.claims.math.helpers.*
import org.apache.commons.math3.util.IntegerSequence

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

data class UnknownDoubleFixedBinWidthDistribution(
        override val average: Double,
        override val minimum: Double,
        override val maximum: Double,
        override val mode: Double,
        override val median: Double,
        override val numberOfBins: Int,
        override val sizeOfBin: Double,
        override val bins: List<FixedWidthBin<Double>>
) : UnknownFixedBinWidthDistribution<Double> {

    override fun random(): Double {
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

fun List<Double>.unknownDistribution(
        numberOfBins: Int = this.size.toDouble().sqrt().ceiling().toInt(),
        rangeMinimum: Double? = null,
        rangeMaximum: Double? = null
): UnknownFixedBinWidthDistribution<Double> {

    val sorted = this.sorted()

    val sortedAndFilteredByMinimum: List<Double> = rangeMinimum
            ?.let { sorted.filterNot { it < rangeMinimum } }
            ?: sorted

    val sortedAndFilteredByMinimumAndMaximum: List<Double> = rangeMaximum
            ?.let { sortedAndFilteredByMinimum.filterNot { it > rangeMaximum } }
            ?: sortedAndFilteredByMinimum

    val observedMinimum = sortedAndFilteredByMinimumAndMaximum.min() ?: 0.0
    val observedMaximum = sortedAndFilteredByMinimumAndMaximum.max() ?: 0.0

    val actualMinimum = rangeMinimum ?: observedMinimum
    val actualMaximum = rangeMaximum ?: observedMaximum

    val range = actualMaximum - actualMinimum

    val sizeOfBin = range / numberOfBins

    var initialBins = listOf(FixedWidthBin(startValue = actualMinimum, count = 0))

    while (initialBins.last().startValue + sizeOfBin < actualMaximum) {

        initialBins += FixedWidthBin(startValue = initialBins.last().startValue + sizeOfBin, count = 0)
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

    return UnknownDoubleFixedBinWidthDistribution(
            average = sorted.average(),
            minimum = observedMinimum,
            maximum = observedMaximum,
            mode = sorted.mode(),
            median = sorted.median(),
            numberOfBins = bins.count(),
            sizeOfBin = sizeOfBin.toDouble(),
            bins = bins.map { FixedWidthBin(startValue = it.startValue, count = it.count) }
    )
}

//fun List<Double>.unknownDistributionApacheCommons(
//        numberOfBins: Int = Math.ceil(Math.sqrt(this.size.toDouble())).toInt(),
//        rangeMinimum: Double? = null,
//        rangeMaximum: Double? = null
//): UnknownFixedBinWidthDistribution<Double> {
//
//    val sorted = this.sorted()
//
//    val sortedAndFilteredByMinimum: List<Double> = rangeMinimum
//            ?.let { sorted.filterNot { it < rangeMinimum } }
//            ?: sorted
//
//    val sortedAndFilteredByMinimumAndMaximum: List<Double> = rangeMaximum
//            ?.let { sortedAndFilteredByMinimum.filterNot { it > rangeMaximum } }
//            ?: sortedAndFilteredByMinimum
//
//    val observedMinimum = sortedAndFilteredByMinimumAndMaximum.min() ?: 0.0
//    val observedMaximum = sortedAndFilteredByMinimumAndMaximum.max() ?: 0.0
//
//    val actualMinimum = rangeMinimum ?: observedMinimum
//    val actualMaximum = rangeMaximum ?: observedMaximum
//
//    val range = actualMaximum - actualMinimum
//
//    val preliminarySizeOfBin = (range / numberOfBins).ceiling().toInt()
//
//    val sizeOfBin = if (preliminarySizeOfBin == 0) 1 else preliminarySizeOfBin
//
//    val initialBins =  IntegerSequence.Range(Math.floor(actualMinimum).toInt(), Math.ceil(actualMaximum).toInt(), sizeOfBin).toList().map {
//        FixedWidthBin(startValue = it.toDouble(), count = 0)
//    }
//
//    val bins = sortedAndFilteredByMinimumAndMaximum.fold(initialBins) { bins, int ->
//
//        val binNumber = (int - actualMinimum) / sizeOfBin
//
//        val startValue = binNumber * sizeOfBin + actualMinimum
//
//        var binExist = false
//
//        val binCopy = bins.map {
//            if (it.startValue == startValue) {
//                binExist = true
//                it.copy(count = it.count + 1)
//            } else {
//                it
//            }
//        }
//
//        if (binExist) {
//            binCopy
//        } else {
//            bins.plus(FixedWidthBin(startValue = startValue, count = 1))
//        }
//    }.sortedBy { it.startValue }
//
//    return UnknownDoubleFixedBinWidthDistribution(
//            average = sorted.average(),
//            minimum = observedMinimum,
//            maximum = observedMaximum,
//            mode = sorted.mode(),
//            median = sorted.median(),
//            numberOfBins = bins.count(),
//            sizeOfBin = sizeOfBin.toDouble(),
//            bins = bins.map { FixedWidthBin(startValue = it.startValue, count = it.count) }
//    )
//}