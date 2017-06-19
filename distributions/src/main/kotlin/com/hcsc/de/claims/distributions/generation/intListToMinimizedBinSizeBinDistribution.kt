package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.distributions.binDistributions.BinDistribution
import com.hcsc.de.claims.distributions.binDistributions.IntBinWithMembersDistribution
import com.hcsc.de.claims.distributions.bins.BinWithMembers
import com.hcsc.de.claims.distributions.bins.IntBinWithMembers
import com.hcsc.de.claims.results.Result

fun List<Int>.minimizedBinSizeBinDistribution(
        minimumBinSize: Int = 5,
        rangeMinimum: Int? = null,
        rangeMaximum: Int? = null
): Result<String, BinDistribution<Int, BinWithMembers<Int>>> =
        genericMinimizedBinSizeBinDistribution(
                minimumBinSize = minimumBinSize,
                rangeMinimum = rangeMinimum,
                rangeMaximum = rangeMaximum,
                toType = { Math.round(this).toInt() }
        )