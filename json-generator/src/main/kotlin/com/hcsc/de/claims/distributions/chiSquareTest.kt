package com.hcsc.de.claims.distributions

import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.Success
import org.apache.commons.math.util.FastMath
import org.apache.commons.math3.distribution.ChiSquaredDistribution

data class ChiSquareValue(
        val statistic: Double,
        val pValue: Double
)

fun List<Int>.chiSquaredTestFromList(expected: List<Int>, binCount: Int = 5): Result<String, ChiSquareValue> {


    val observedMin = this.min() ?: 0
    val observedMax = this.max() ?: 0

    val expectedMin = expected.min() ?: 0
    val expectedMax = expected.max() ?: 0

    val rangeMinimum = if (observedMin < expectedMin) observedMin else expectedMin
    val rangeMaximum = if (observedMax > expectedMax) observedMax else expectedMax

    val expectedUnknownDistribution = expected.unknownDistribution(
            numberOfBins = binCount,
            rangeMinimum = rangeMinimum,
            rangeMaximum = rangeMaximum
    )

    val observedUnknownDistribution = this.unknownDistribution(
            numberOfBins = binCount,
            rangeMinimum = rangeMinimum,
            rangeMaximum = rangeMaximum
    )

    return observedUnknownDistribution.chiSquaredTest(expectedUnknownDistribution)

}

fun DistributionPair<Int>.chiSquaredTest(binCount: Int = 10): Result<String, ChiSquareValue> {

    val (distOne, distTwo) = this.unknownDualMemberVariableBinWidthDistribution(binCount).asTwoDistributions

    return distOne.chiSquaredTest(distTwo)
}

private fun BinDistribution<Int>.chiSquaredTest(expected: BinDistribution<Int>): Result<String, ChiSquareValue> {

    val sumExpected = expected.bins.fold(0) { acc, bin -> acc + bin.count }
    val sumObserved = this.bins.fold(0) { acc, bin -> acc + bin.count }

    val chiSquareStatistic = if (FastMath.abs(sumExpected - sumObserved) > 10E-6) {

        val ratio = sumObserved.toDouble() / sumExpected

        this.bins.map(Bin::count)
                .zip(expected.bins.map(Bin::count))
                .fold(0.0) { acc, (observed, expected) ->
                    val deviance = observed - ratio * expected
                    acc + deviance * deviance / (ratio * expected)
                }
    } else {
        this.bins.map(Bin::count)
                .zip(expected.bins.map(Bin::count))
                .fold(0.0) { acc, (observed, expected) ->
                    val deviance = (observed - expected).toDouble()
                    acc + deviance * deviance / expected
                }
    }

    val chiSquaredDistribution = ChiSquaredDistribution(null, (expected.bins.size - 1).toDouble())

    val pValue = 1.0 - chiSquaredDistribution.cumulativeProbability(chiSquareStatistic)

    return Success(ChiSquareValue(
            statistic = chiSquareStatistic,
            pValue = pValue
    ))
}
