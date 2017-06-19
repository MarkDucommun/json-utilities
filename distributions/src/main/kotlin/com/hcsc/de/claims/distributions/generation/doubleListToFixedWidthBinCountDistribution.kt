package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.distributions.binDistributions.FixedWidthBinDistribution
import com.hcsc.de.claims.distributions.bins.Bin
import com.hcsc.de.claims.results.Result

fun List<Double>.toFixedWidthBinCountDistribution(
        binCount: Int = 5,
        rangeMinimum: Double? = null,
        rangeMaximum: Double? = null
): Result<String, FixedWidthBinDistribution<Double, Bin<Double>>> =
        genericFixedWidthBinCountDistribution(
                binCount,
                rangeMinimum = rangeMinimum,
                rangeMaximum = rangeMaximum,
                toType = { this }
        )