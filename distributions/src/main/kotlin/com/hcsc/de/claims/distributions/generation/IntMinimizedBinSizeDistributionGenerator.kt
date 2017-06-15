package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.distributions.binDistributions.IntBinWithMembersDistribution
import com.hcsc.de.claims.distributions.bins.IntBinWithMembers

class IntMinimizedBinSizeDistributionGenerator : MinimizedBinSizeDistributionGenerator<Int>(
        toBinNumberType = { IntBinWithMembers(members = this.members.map(Double::toInt)) },
        toBinDistribution = { IntBinWithMembersDistribution(bins = this) }
)