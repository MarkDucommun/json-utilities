package com.hcsc.de.claims.distributions.binDistributions

import com.hcsc.de.claims.distributions.bins.BinWithMembers

open class FixedWidthBinWithMembersDistribution<numberType : Number>(
        rawBins: List<BinWithMembers<numberType>>,
        override val binWidth: numberType,
        toType: Double.() -> numberType
) : BinWithMembersDistribution<numberType, BinWithMembers<numberType>>(
        rawBins = rawBins,
        toType = toType
), FixedWidthBinDistribution<numberType, BinWithMembers<numberType>>