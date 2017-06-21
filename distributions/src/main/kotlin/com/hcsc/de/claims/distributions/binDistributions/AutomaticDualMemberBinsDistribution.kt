package com.hcsc.de.claims.distributions.binDistributions

import com.hcsc.de.claims.distributions.bins.BinWithMembers
import com.hcsc.de.claims.distributions.bins.DualSourceBinWithMembers

open class AutomaticDualMemberBinsDistribution<numberType : Number>(
        rawBins: List<DualSourceBinWithMembers<numberType, BinWithMembers<numberType>>>,
        val toType: Double.() -> numberType
) : BinWithMembersDistribution<numberType, DualSourceBinWithMembers<numberType, BinWithMembers<numberType>>>(
        rawBins = rawBins,
        toType = toType
), DualMemberBinsDistribution<numberType> {

    override val asTwoDistributions: Pair<
            BinDistribution<numberType, BinWithMembers<numberType>>,
            BinDistribution<numberType, BinWithMembers<numberType>>>
        get() = BinWithMembersDistribution(rawBins = bins.map { it.sourceOneBin }, toType = toType) to
                BinWithMembersDistribution(rawBins = bins.map { it.sourceTwoBin }, toType = toType)
}