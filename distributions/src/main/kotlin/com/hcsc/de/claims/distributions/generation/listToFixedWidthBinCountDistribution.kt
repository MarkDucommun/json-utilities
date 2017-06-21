package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.collection.helpers.*
import com.hcsc.de.claims.distributions.binDistributions.FixedWidthBinDistribution
import com.hcsc.de.claims.distributions.binDistributions.GenericFixedWidthBinDistribution
import com.hcsc.de.claims.distributions.bins.Bin
import com.hcsc.de.claims.results.Result
import com.hcsc.de.claims.results.map

fun <numberType : Number> List<numberType>.genericFixedWidthBinCountDistribution(
        binCount: Int = 5,
        rangeMinimum: numberType? = null,
        rangeMaximum: numberType? = null,
        toType: Double.() -> numberType
): Result<String, FixedWidthBinDistribution<numberType, Bin<numberType>>> {

    return map(Number::toDouble)
            .sorted()
            .filterNotLessThan(rangeMinimum?.toDouble())
            .filterNotGreaterThan(rangeMaximum?.toDouble())
            .asNonEmptyList()
            .map { it.map(toType) }
            .map { it.genericFixedWidthBinCountDistribution(binCount = binCount, toType = toType) }
}

fun <numberType: Number> NonEmptyList<numberType>.genericFixedWidthBinCountDistribution(
        binCount: Int = 5,
        toType: Double.() -> numberType
): FixedWidthBinDistribution<numberType, Bin<numberType>> {

    val doubleList = map(Number::toDouble).sorted()

    val minimum = doubleList.minimum()
    val maximum = doubleList.maximum()

    // TODO go back and make the minimum bin width be 1 elsewhere in the code
    val binWidth = (maximum - minimum + 1) / binCount

    val bins = doubleList.toFixedWidthBins(binWidth = binWidth, minimum = minimum)

    return GenericFixedWidthBinDistribution(
            average = doubleList.average().toType(),
            minimum = minimum.toType(),
            maximum = maximum.toType(),
            mode = doubleList.simpleMode().toType(),
            median = doubleList.median().toType(),
            binWidth = binWidth.toType(),
            bins = bins.map { it.toBinType(toType) }
    )
}