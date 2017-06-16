package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.collection.helpers.filterNotGreaterThan
import com.hcsc.de.claims.collection.helpers.filterNotLessThan
import com.hcsc.de.claims.distributions.binDistributions.FixedWidthBinDistribution
import com.hcsc.de.claims.distributions.binDistributions.GenericFixedWidthBinDistribution
import com.hcsc.de.claims.distributions.bins.Bin
import com.hcsc.de.claims.distributions.bins.SimpleBin
import com.hcsc.de.claims.math.helpers.median
import com.hcsc.de.claims.math.helpers.mode
import kotlin.collections.average

fun List<Double>.toFixedWidthBinCountDistribution(
        binCount: Int = 5,
        rangeMinimum: Double? = null,
        rangeMaximum: Double? = null
): FixedWidthBinDistribution<Double, Bin<Double>> =
        genericFixedWidthBinCountDistribution(
                binCount,
                rangeMinimum = rangeMinimum,
                rangeMaximum = rangeMaximum,
                toType = { this }
        )

fun List<Int>.toFixedWidthBinCountDistribution(
        binCount: Int = 5,
        rangeMinimum: Int? = null,
        rangeMaximum: Int? = null
): FixedWidthBinDistribution<Int, Bin<Int>> =
        genericFixedWidthBinCountDistribution(
                binCount,
                rangeMinimum = rangeMinimum,
                rangeMaximum = rangeMaximum,
                toType = { Math.round(this).toInt() }
        )

fun <numberType : Number> List<numberType>.genericFixedWidthBinCountDistribution(
        binCount: Int = 5,
        rangeMinimum: numberType? = null,
        rangeMaximum: numberType? = null,
        toType: Double.() -> numberType
): FixedWidthBinDistribution<numberType, Bin<numberType>> {

    val sortedAndFiltered = map(Number::toDouble)
            .sorted()
            .filterNotLessThan(rangeMinimum?.toDouble())
            .filterNotGreaterThan(rangeMaximum?.toDouble())

    val observedMinimum = sortedAndFiltered.min() ?: 0.0
    val observedMaximum = sortedAndFiltered.max() ?: 0.0

    val actualMinimum = (rangeMinimum?.toDouble() ?: observedMinimum).toType().toDouble()
    val actualMaximum = (rangeMaximum?.toDouble() ?: observedMaximum).toType().toDouble()

    val binWidth = (actualMaximum - actualMinimum + 1) / binCount

    val bins = sortedAndFiltered.toFixedWidthBins(binWidth = binWidth, minimum = actualMinimum)

    return GenericFixedWidthBinDistribution<numberType>(
            average = sortedAndFiltered.average().toType(),
            minimum = observedMinimum.toType(),
            maximum = observedMaximum.toType(),
            mode = sortedAndFiltered.mode().toType(),
            median = sortedAndFiltered.median().toType(),
            binWidth = binWidth.toType(),
            bins = bins.map { it.toBinType(toType) }
    )
}

fun List<Double>.toFixedWidthBinDistribution(
        binWidth: Double,
        rangeMinimum: Double? = null,
        rangeMaximum: Double? = null
): FixedWidthBinDistribution<Double, Bin<Double>> =
        genericFixedWidthBinDistribution(
                binWidth = binWidth,
                rangeMinimum = rangeMinimum,
                rangeMaximum = rangeMaximum,
                toType = { this }
        )

fun List<Int>.toFixedWidthBinDistribution(
        binWidth: Int,
        rangeMinimum: Int? = null,
        rangeMaximum: Int? = null
): FixedWidthBinDistribution<Int, Bin<Int>> =
        genericFixedWidthBinDistribution(
                binWidth = binWidth,
                rangeMinimum = rangeMinimum,
                rangeMaximum = rangeMaximum,
                toType = { Math.round(this).toInt() }
        )

fun <numberType : Number> List<numberType>.genericFixedWidthBinDistribution(
        binWidth: numberType,
        rangeMinimum: numberType? = null,
        rangeMaximum: numberType? = null,
        toType: Double.() -> numberType
): FixedWidthBinDistribution<numberType, Bin<numberType>> {

    val doubleBinWidth = binWidth.toDouble()

    val sortedAndFiltered = map(Number::toDouble)
            .sorted()
            .filterNotLessThan(rangeMinimum?.toDouble())
            .filterNotGreaterThan(rangeMaximum?.toDouble())

    val observedMinimum = sortedAndFiltered.min() ?: 0.0
    val observedMaximum = sortedAndFiltered.max() ?: 0.0

    val actualMinimum = (rangeMinimum?.toDouble() ?: observedMinimum).toType().toDouble()

    val bins = sortedAndFiltered.toFixedWidthBins(binWidth = doubleBinWidth, minimum = actualMinimum)

    return GenericFixedWidthBinDistribution<numberType>(
            average = sortedAndFiltered.average().toType(),
            minimum = observedMinimum.toType(),
            maximum = observedMaximum.toType(),
            mode = sortedAndFiltered.mode().toType(),
            median = sortedAndFiltered.median().toType(),
            binWidth = binWidth,
            bins = bins.map { it.toBinType(toType) }
    )
}

private fun List<Double>.toFixedWidthBins(binWidth: Double, minimum: Double): List<SimpleBin<Double>> {

    return fold(emptyList<SimpleBin<Double>>()) { bins, int ->

        val binNumber = ((int - minimum) / binWidth).toInt()

        val startValue = binNumber * binWidth + minimum

        bins
                .filterNot { it.identifyingCharacteristic == startValue }
                .plus(bins.incrementAppropriateBin(startValue))

    }.sortedBy { it.identifyingCharacteristic }
}

private fun List<SimpleBin<Double>>.incrementAppropriateBin(startValue: Double): SimpleBin<Double> =
        find { it.identifyingCharacteristic == startValue }
                ?.incrementSize()
                ?: SimpleBin(identifyingCharacteristic = startValue, size = 1)

private fun <startNumberType : Number, endNumberType: Number> SimpleBin<startNumberType>.toBinType(toType: startNumberType.() -> endNumberType) =
        SimpleBin(identifyingCharacteristic = identifyingCharacteristic.toType(), size = size)