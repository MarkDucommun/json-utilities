package com.hcsc.de.claims.fitChecking

import com.hcsc.de.claims.distributions.binDistributions.BinDistribution
import com.hcsc.de.claims.distributions.bins.Bin
import com.hcsc.de.claims.distributions.generation.unknownDistribution
import com.hcsc.de.claims.distributions.generation.idealBinCountMinimumSizedDualMemberBinDistribution
import org.apache.commons.math3.distribution.ChiSquaredDistribution
import umontreal.ssj.gof.GofStat
import umontreal.ssj.probdist.ChiSquareDist

fun List<Int>.chiSquaredTestFromList(expected: List<Int>, binCount: Int = 5): com.hcsc.de.claims.results.Result<String, ChiSquareValue> {


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

fun <numberType : Number> com.hcsc.de.claims.distributions.DistributionPair<numberType>.chiSquaredTest(binCount: Int = 10): com.hcsc.de.claims.results.Result<String, ChiSquareValue> {

    val (distOne, distTwo) = this.idealBinCountMinimumSizedDualMemberBinDistribution(binCount).asTwoDistributions

    val actualBins = distOne.bins.count()
    print("actualBins: $actualBins, ")

    return distOne.doubleChiSquaredTest(distTwo)
}

private fun BinDistribution<Int, Bin<Int>>.chiSquaredTest(expected: BinDistribution<Int, Bin<Int>>): com.hcsc.de.claims.results.Result<String, ChiSquareValue> {

    val sumExpected = expected.bins.fold(0) { acc, bin -> acc + bin.size }
    val sumObserved = this.bins.fold(0) { acc, bin -> acc + bin.size }

    val chiSquareStatistic = if (org.apache.commons.math.util.FastMath.abs(sumExpected - sumObserved) > 10E-6) {

        val ratio = sumObserved.toDouble() / sumExpected

        this.bins.map(com.hcsc.de.claims.distributions.bins.Bin<*>::size)
                .zip(expected.bins.map(com.hcsc.de.claims.distributions.bins.Bin<*>::size))
                .fold(0.0) { acc, (observed, expected) ->
                    val deviance = observed - ratio * expected
                    acc + deviance * deviance / (ratio * expected)
                }
    } else {
        this.bins.map(com.hcsc.de.claims.distributions.bins.Bin<*>::size)
                .zip(expected.bins.map(com.hcsc.de.claims.distributions.bins.Bin<*>::size))
                .fold(0.0) { acc, (observed, expected) ->
                    val deviance = (observed - expected).toDouble()
                    acc + deviance * deviance / expected
                }
    }

    val montrealpValue = umontreal.ssj.gof.GofStat.pDisc(umontreal.ssj.probdist.ChiSquareDist.cdf(bins.size - 1, 2, chiSquareStatistic),
            umontreal.ssj.probdist.ChiSquareDist.barF(bins.size - 1, 2, chiSquareStatistic))

    val chiSquaredDistribution = org.apache.commons.math3.distribution.ChiSquaredDistribution(null, (expected.bins.size - 1).toDouble())

    val pValue = 1.0 - chiSquaredDistribution.cumulativeProbability(chiSquareStatistic)

    println("Chi-Square: $chiSquareStatistic, Montreal: $montrealpValue, JDistLib: $pValue")

    return com.hcsc.de.claims.results.Success(com.hcsc.de.claims.distributions.ChiSquareValue(
            statistic = chiSquareStatistic,
            pValue = montrealpValue
    ))
}

fun List<Double>.chiSquaredTestFromDoubleList(expected: List<Double>, binCount: Int = 5): com.hcsc.de.claims.results.Result<String, ChiSquareValue> {


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

fun com.hcsc.de.claims.distributions.DistributionPair<Double>.doubleChiSquaredTest(binCount: Int = 10): com.hcsc.de.claims.results.Result<String, ChiSquareValue> {

    val (distOne, distTwo) = this.idealBinCountMinimumSizedDualMemberBinDistribution(binCount).asTwoDistributions

    return distOne.doubleChiSquaredTest(distTwo)
}

private fun BinDistribution<Double, Bin<Double>>.doubleChiSquaredTest(expected: BinDistribution<Double, Bin<Double>>): com.hcsc.de.claims.results.Result<String, ChiSquareValue> {

    val sumExpected = expected.bins.fold(0) { acc, bin -> acc + bin.size }
    val sumObserved = this.bins.fold(0) { acc, bin -> acc + bin.size }

    val sortedFirstBins = this.bins
    val sortedSecondBins = expected.bins

    val zip = sortedFirstBins.map { bin ->
        bin.size to (sortedSecondBins.find { it.identifyingCharacteristic == bin.identifyingCharacteristic }?.size ?: throw RuntimeException())
    }

    val chiSquareStatistic = if (org.apache.commons.math.util.FastMath.abs(sumExpected - sumObserved) > 10E-6) {

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


    return com.hcsc.de.claims.results.wrapExternalLibraryUsageAsResult {

        val montrealChiSquareStatistic = GofStat.chi2(expectedDoubles.toDoubleArray(), observedInts.toIntArray(), 0, zip.size - 1)

        val montrealpValue = GofStat.pDisc(umontreal.ssj.probdist.ChiSquareDist.cdf(bins.size - 1, 2, montrealChiSquareStatistic),
                ChiSquareDist.barF(bins.size - 1, 2, montrealChiSquareStatistic))

        val chiSquaredDistribution = ChiSquaredDistribution(null, (expected.bins.size - 1).toDouble())

        val pValue = 1.0 - chiSquaredDistribution.cumulativeProbability(chiSquareStatistic)

        println("Chi-Square: $chiSquareStatistic, Montreal: $montrealpValue, JDistLib: $pValue")

        ChiSquareValue(statistic = chiSquareStatistic, pValue = montrealpValue)
    }
}
