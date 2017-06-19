package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.distributions.binDistributions.FixedWidthBinDistribution
import com.hcsc.de.claims.distributions.bins.Bin
import com.hcsc.de.claims.results.Result

fun List<Int>.toFixedWidthBinDistribution(
        binWidth: Int,
        rangeMinimum: Int? = null,
        rangeMaximum: Int? = null
): Result<String, FixedWidthBinDistribution<Int, Bin<Int>>> =
        genericFixedWidthBinDistribution(
                binWidth = binWidth,
                rangeMinimum = rangeMinimum,
                rangeMaximum = rangeMaximum,
                toType = { Math.round(this).toInt() }
        )