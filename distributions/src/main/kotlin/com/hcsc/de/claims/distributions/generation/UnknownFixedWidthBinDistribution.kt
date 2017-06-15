package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.distributions.binDistributions.AutomaticFixedWidthBinDistribution
import com.hcsc.de.claims.distributions.binDistributions.FixedWidthBinDistribution
import com.hcsc.de.claims.distributions.bins.SimpleBin
import com.hcsc.de.claims.math.helpers.*
import org.apache.commons.math3.util.IntegerSequence

fun List<Int>.unknownDistribution(
        numberOfBins: Int = 5,
        rangeMinimum: Int? = null,
        rangeMaximum: Int? = null
): FixedWidthBinDistribution<Int> {

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
        SimpleBin(size = 0, identifyingCharacteristic = it)
    }

    val bins = sortedAndFilteredByMinimumAndMaximum.fold(initialBins) { bins, int ->

        val binNumber = (int - actualMinimum) / sizeOfBin

        val startValue = binNumber * sizeOfBin + actualMinimum

        var binExist = false

        val binCopy = bins.map {
            if (it.identifyingCharacteristic == startValue) {
                binExist = true
                it.copy(size = it.size + 1)
            } else {
                it
            }
        }

        if (binExist) {
            binCopy
        } else {
            bins.plus(SimpleBin(identifyingCharacteristic = startValue, size = 1))
        }
    }.sortedBy { it.identifyingCharacteristic }

    return AutomaticFixedWidthBinDistribution(
            average = sorted.averageInt(),
            minimum = observedMinimum,
            maximum = observedMaximum,
            mode = sorted.modeInt(),
            median = sorted.medianInt(),
            binWidth = sizeOfBin,
            rawBins = bins
    )
}

fun List<Double>.unknownDistribution(
        numberOfBins: Int = this.size.toDouble().sqrt().ceiling().toInt(),
        rangeMinimum: Double? = null,
        rangeMaximum: Double? = null
): FixedWidthBinDistribution<Double> {

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

    val binWidth = range / numberOfBins

    var initialBins = listOf(SimpleBin(identifyingCharacteristic = actualMinimum, size = 0))

    while (initialBins.last().identifyingCharacteristic + binWidth < actualMaximum) {

        initialBins += SimpleBin(identifyingCharacteristic = initialBins.last().identifyingCharacteristic + binWidth, size = 0)
    }

    val bins = sortedAndFilteredByMinimumAndMaximum.fold(initialBins) { bins, int ->

        val binNumber = (int - actualMinimum) / binWidth

        val startValue = binNumber * binWidth + actualMinimum

        var binExist = false

        val binCopy = bins.map {
            if (it.identifyingCharacteristic == startValue) {
                binExist = true
                it.copy(size = it.size + 1)
            } else {
                it
            }
        }

        if (binExist) {
            binCopy
        } else {
            bins.plus(SimpleBin(identifyingCharacteristic = startValue, size = 1))
        }
    }.sortedBy { it.identifyingCharacteristic }

    return AutomaticFixedWidthBinDistribution(
            average = sorted.average(),
            minimum = observedMinimum,
            maximum = observedMaximum,
            mode = sorted.mode(),
            median = sorted.median(),
            binWidth = binWidth,
            rawBins = bins
    )
}

//fun List<Double>.unknownDistributionApacheCommons(
//        numberOfBins: Int = Math.ceil(Math.sqrt(this.size.toDouble())).toInt(),
//        rangeMinimum: Double? = null,
//        rangeMaximum: Double? = null
//): FixedWidthBinDistribution<Double> {
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
//    val binWidth = if (preliminarySizeOfBin == 0) 1 else preliminarySizeOfBin
//
//    val initialBins =  IntegerSequence.Range(Math.floor(actualMinimum).toInt(), Math.ceil(actualMaximum).toInt(), binWidth).toList().map {
//        FixedWidthBin(startValue = it.toDouble(), size = 0)
//    }
//
//    val bins = sortedAndFilteredByMinimumAndMaximum.fold(initialBins) { bins, int ->
//
//        val binNumber = (int - actualMinimum) / binWidth
//
//        val startValue = binNumber * binWidth + actualMinimum
//
//        var binExist = false
//
//        val binCopy = bins.map {
//            if (it.startValue == startValue) {
//                binExist = true
//                it.copy(size = it.size + 1)
//            } else {
//                it
//            }
//        }
//
//        if (binExist) {
//            binCopy
//        } else {
//            bins.plus(FixedWidthBin(startValue = startValue, size = 1))
//        }
//    }.sortedBy { it.startValue }
//
//    return DoubleFixedBinWidthDistribution(
//            average = sorted.average(),
//            minimum = observedMinimum,
//            maximum = observedMaximum,
//            mode = sorted.mode(),
//            median = sorted.median(),
//            numberOfBins = bins.size(),
//            binWidth = binWidth.toDouble(),
//            bins = bins.map { FixedWidthBin(startValue = it.startValue, size = it.size) }
//    )
//}