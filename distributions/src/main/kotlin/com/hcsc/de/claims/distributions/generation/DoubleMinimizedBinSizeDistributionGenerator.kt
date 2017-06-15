package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.distributions.binDistributions.DoubleBinWithMembersDistribution

class DoubleMinimizedBinSizeDistributionGenerator : MinimizedBinSizeDistributionGenerator<Double>(
        toBinNumberType = { this },
        toBinDistribution = { DoubleBinWithMembersDistribution(this) }
)