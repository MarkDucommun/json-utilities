package com.hcsc.de.claims.distributions.binDistributions

import com.hcsc.de.claims.distributions.bins.BinWithMembers
import com.hcsc.de.claims.distributions.bins.DualMemberBin

open class AutomaticDualMemberBinsDistribution<numberType : Number>(
        rawBins: List<DualMemberBin<numberType, BinWithMembers<numberType>>>,
        val toType: Double.() -> numberType
) : BinWithMembersDistribution<numberType, DualMemberBin<numberType, BinWithMembers<numberType>>>(
        rawBins = rawBins,
        toType = toType
), DualMemberBinsDistribution<numberType> {

    override val asTwoDistributions: Pair<
            BinDistribution<numberType, BinWithMembers<numberType>>,
            BinDistribution<numberType, BinWithMembers<numberType>>>
        get() = BinWithMembersDistribution(rawBins = bins.map { it.binOne }, toType = toType) to
                BinWithMembersDistribution(rawBins = bins.map { it.binTwo }, toType = toType)
}