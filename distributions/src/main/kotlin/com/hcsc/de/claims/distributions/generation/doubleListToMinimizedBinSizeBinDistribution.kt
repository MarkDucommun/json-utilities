package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.distributions.binDistributions.BinDistribution
import com.hcsc.de.claims.distributions.binDistributions.DoubleBinWithMembersDistribution
import com.hcsc.de.claims.distributions.bins.BinWithMembers
import com.hcsc.de.claims.results.Result

fun List<Double>.minimizedBinSizeBinDistribution(
        minimumBinSize: Int = 5,
        rangeMinimum: Double? = null,
        rangeMaximum: Double? = null
): Result<String, BinDistribution<Double, BinWithMembers<Double>>> =
        genericMinimizedBinSizeBinDistribution(
                minimumBinSize = minimumBinSize,
                rangeMinimum = rangeMinimum,
                rangeMaximum = rangeMaximum,
                toType = { this }
        )