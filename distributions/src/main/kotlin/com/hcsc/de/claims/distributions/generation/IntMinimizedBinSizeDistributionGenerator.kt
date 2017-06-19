package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.distributions.binDistributions.IntBinWithMembersDistribution
import com.hcsc.de.claims.distributions.bins.IntBinWithMembers

class IntMinimizedBinSizeDistributionGenerator : MinimizedBinSizeDistributionGenerator<Int>(
        toType = { Math.round(this).toInt() }
)