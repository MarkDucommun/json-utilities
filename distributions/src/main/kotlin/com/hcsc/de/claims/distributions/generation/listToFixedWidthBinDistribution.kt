package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.collection.helpers.*
import com.hcsc.de.claims.distributions.binDistributions.FixedWidthBinDistribution
import com.hcsc.de.claims.distributions.binDistributions.GenericFixedWidthBinDistribution
import com.hcsc.de.claims.distributions.bins.Bin
import com.hcsc.de.claims.distributions.bins.SimpleBin
import com.hcsc.de.claims.results.Result
import com.hcsc.de.claims.results.map

fun <numberType: Number> List<numberType>.genericFixedWidthBinDistribution(
        binWidth: numberType,
        rangeMinimum: numberType? = null,
        rangeMaximum: numberType? = null,
        toType: Double.() -> numberType
): Result<String, FixedWidthBinDistribution<numberType, Bin<numberType>>> {

    return map(Number::toDouble)
            .filterNotLessThan(rangeMinimum?.toDouble())
            .filterNotGreaterThan(rangeMaximum?.toDouble())
            .asNonEmptyList()
            .map { it.map(toType) }
            .map { it.genericFixedWidthBinDistribution(binWidth = binWidth, toType = toType) }
}

fun <numberType : Number> NonEmptyList<numberType>.genericFixedWidthBinDistribution(
        binWidth: numberType,
        toType: Double.() -> numberType
): FixedWidthBinDistribution<numberType, Bin<numberType>> {

    val doubleBinWidth = binWidth.toDouble()

    val doubleList = map(Number::toDouble)

    val bins = doubleList.toFixedWidthBins(binWidth = doubleBinWidth, minimum = minimum(toType).toDouble())

    return GenericFixedWidthBinDistribution<numberType>(
            average = doubleList.average().toType(),
            minimum = doubleList.minimum().toType(),
            maximum = doubleList.maximum().toType(),
            mode = doubleList.simpleMode().toType(),
            median = doubleList.median().toType(),
            binWidth = binWidth,
            bins = bins.map { it.toBinType(toType) }
    )
}

internal fun NonEmptyList<Double>.toFixedWidthBins(binWidth: Double, minimum: Double): List<SimpleBin<Double>> =

        fold(emptyList<SimpleBin<Double>>()) { bins, int ->

            val binNumber = ((int - minimum) / binWidth).toInt()

            val startValue = binNumber * binWidth + minimum

            bins
                    .filterNot { it.identifyingCharacteristic == startValue }
                    .plus(bins.incrementAppropriateBin(startValue))

        }.sortedBy { it.identifyingCharacteristic }

internal fun List<SimpleBin<Double>>.incrementAppropriateBin(startValue: Double): SimpleBin<Double> =
        find { it.identifyingCharacteristic == startValue }
                ?.incrementSize()
                ?: SimpleBin(identifyingCharacteristic = startValue, size = 1)

internal fun <startNumberType : Number, endNumberType : Number>
        SimpleBin<startNumberType>.toBinType(toType: startNumberType.() -> endNumberType) =
        SimpleBin(identifyingCharacteristic = identifyingCharacteristic.toType(), size = size)