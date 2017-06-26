package com.hcsc.de.claims.fitChecking

import com.hcsc.de.claims.distributions.binDistributions.BinDistribution
import com.hcsc.de.claims.distributions.bins.Bin
import com.hcsc.de.claims.results.Result
import com.hcsc.de.claims.results.wrapExternalLibraryUsageAsResult
import org.apache.commons.math.util.FastMath.abs
import org.apache.commons.math3.distribution.ChiSquaredDistribution
import umontreal.ssj.gof.GofStat
import umontreal.ssj.gof.GofStat.pDisc
import umontreal.ssj.probdist.ChiSquareDist


fun <numberType: Number> BinDistribution<numberType, Bin<numberType>>.chiSquaredTest(
        expected: BinDistribution<numberType, Bin<numberType>>
): Result<String, ChiSquareValue> {

    val sumExpected = expected.bins.fold(0) { acc, bin -> acc + bin.size }
    val sumObserved = this.bins.fold(0) { acc, bin -> acc + bin.size }

    val sortedFirstBins = this.bins
    val sortedSecondBins = expected.bins

    val zip = sortedFirstBins.map { bin ->
        bin.size to (sortedSecondBins.find { it.identifyingCharacteristic == bin.identifyingCharacteristic }?.size ?: throw RuntimeException())
    }

    val chiSquareStatistic = if (abs(sumExpected - sumObserved) > 10E-6) {

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

        val montrealpValue = pDisc(ChiSquareDist.cdf(bins.size - 1, 2, montrealChiSquareStatistic),
                ChiSquareDist.barF(bins.size - 1, 2, montrealChiSquareStatistic))

        val chiSquaredDistribution = ChiSquaredDistribution(null, (expected.bins.size - 1).toDouble())

        val pValue = 1.0 - chiSquaredDistribution.cumulativeProbability(chiSquareStatistic)

        println("Chi-Square: $chiSquareStatistic, Montreal: $montrealpValue, JDistLib: $pValue")

        ChiSquareValue(statistic = chiSquareStatistic, pValue = montrealpValue)
    }
}
