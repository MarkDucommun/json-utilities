package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.distributions.binDistributions.FixedWidthBinDistribution
import com.hcsc.de.claims.distributions.bins.Bin
import com.hcsc.de.claims.results.Result

fun List<Double>.toFixedWidthBinDistribution(
        binWidth: Double,
        rangeMinimum: Double? = null,
        rangeMaximum: Double? = null
): Result<String, FixedWidthBinDistribution<Double, Bin<Double>>> =
        genericFixedWidthBinDistribution(
                binWidth = binWidth,
                rangeMinimum = rangeMinimum,
                rangeMaximum = rangeMaximum,
                toType = { this }
        )