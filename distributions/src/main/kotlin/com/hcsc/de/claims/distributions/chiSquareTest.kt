package com.hcsc.de.claims.distributions

import com.hcsc.de.claims.results.Result
import com.hcsc.de.claims.results.Success
import com.hcsc.de.claims.results.wrapExternalLibraryUsageAsResult
import org.apache.commons.math.util.FastMath
import org.apache.commons.math3.distribution.ChiSquaredDistribution
import umontreal.ssj.gof.GofStat
import umontreal.ssj.probdist.ChiSquareDist

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

fun <numberType : Number> DistributionPair<numberType>.chiSquaredTest(binCount: Int = 10): Result<String, ChiSquareValue> {

    val (distOne, distTwo) = this.unknownDualMemberVariableBinWidthDistribution(binCount).asTwoDistributions

    val actualBins = distOne.bins.count()
    print("actualBins: $actualBins, ")

    return distOne.doubleChiSquaredTest(distTwo)
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

    val montrealpValue = GofStat.pDisc(ChiSquareDist.cdf(bins.size - 1, 2, chiSquareStatistic),
            ChiSquareDist.barF(bins.size - 1, 2, chiSquareStatistic))

    val chiSquaredDistribution = ChiSquaredDistribution(null, (expected.bins.size - 1).toDouble())

    val pValue = 1.0 - chiSquaredDistribution.cumulativeProbability(chiSquareStatistic)

    println("Chi-Square: $chiSquareStatistic, Montreal: $montrealpValue, JDistLib: $pValue")

    return Success(ChiSquareValue(
            statistic = chiSquareStatistic,
            pValue = montrealpValue
    ))
}

fun List<Double>.chiSquaredTestFromDoubleList(expected: List<Double>, binCount: Int = 5): Result<String, ChiSquareValue> {


    val observedMin = this.min() ?: 0.0
    val observedMax = this.max() ?: 0.0

    val expectedMin = expected.min() ?: 0.0
    val expectedMax = expected.max() ?: 0.0

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

    return observedUnknownDistribution.doubleChiSquaredTest(expectedUnknownDistribution)

}

fun DistributionPair<Double>.doubleChiSquaredTest(binCount: Int = 10): Result<String, ChiSquareValue> {

    val (distOne, distTwo) = this.unknownDualMemberVariableBinWidthDistribution(binCount).asTwoDistributions

    return distOne.doubleChiSquaredTest(distTwo)
}

private fun BinDistribution<Double>.doubleChiSquaredTest(expected: BinDistribution<Double>): Result<String, ChiSquareValue> {

    val sumExpected = expected.bins.fold(0) { acc, bin -> acc + bin.count }
    val sumObserved = this.bins.fold(0) { acc, bin -> acc + bin.count }

    val sortedFirstBins = this.bins
    val sortedSecondBins = expected.bins

    val zip = sortedFirstBins.map { bin ->
        bin.count to (sortedSecondBins.find { it.identifyingCharacteristic == bin.identifyingCharacteristic }?.count ?: throw RuntimeException())
    }

    val chiSquareStatistic = if (FastMath.abs(sumExpected - sumObserved) > 10E-6) {

        val ratio = sumObserved.toDouble() / sumExpected

        zip
                .fold(0.0) { acc, (observed, expected) ->
                    val deviance = observed - ratio * expected
                    acc + deviance * deviance / (ratio * expected)
                }
    } else {
        zip
                .fold(0.0) { acc, (observed, expected) ->
                    val deviance = (observed - expected).toDouble()
                    acc + (deviance * deviance) / expected
                }
    }

    val observedInts = zip.map { it.first }
    val expectedDoubles = zip.map { it.second.toDouble() }


    return wrapExternalLibraryUsageAsResult {

        val montrealChiSquareStatistic = GofStat.chi2(expectedDoubles.toDoubleArray(), observedInts.toIntArray(), 0, zip.size - 1)

        val montrealpValue = GofStat.pDisc(ChiSquareDist.cdf(bins.size - 1, 2, montrealChiSquareStatistic),
                ChiSquareDist.barF(bins.size - 1, 2, montrealChiSquareStatistic))

        val chiSquaredDistribution = ChiSquaredDistribution(null, (expected.bins.size - 1).toDouble())

        val pValue = 1.0 - chiSquaredDistribution.cumulativeProbability(chiSquareStatistic)

        println("Chi-Square: $chiSquareStatistic, Montreal: $montrealpValue, JDistLib: $pValue")

        ChiSquareValue(statistic = chiSquareStatistic, pValue = montrealpValue)
    }
}
