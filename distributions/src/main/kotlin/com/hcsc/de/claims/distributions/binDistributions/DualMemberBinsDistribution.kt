package com.hcsc.de.claims.distributions.binDistributions

import com.hcsc.de.claims.distributions.bins.BinWithMembers
import com.hcsc.de.claims.distributions.bins.DualMemberBin

interface DualMemberBinsDistribution<numberType: Number>
    : BinDistribution<numberType, DualMemberBin<numberType, BinWithMembers<numberType>>> {

    val asTwoDistributions: Pair<
            BinDistribution<numberType, BinWithMembers<numberType>>,
            BinDistribution<numberType, BinWithMembers<numberType>>>
}