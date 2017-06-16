package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.distributions.binDistributions.FixedWidthBinDistribution
import com.hcsc.de.claims.distributions.bins.BinWithMembers
import com.hcsc.de.claims.distributions.generation.DistributionRequest.FixedWidthBinCountDistributionRequest
import com.hcsc.de.claims.results.Result

class FixedWidthBinCountWithMembersDistributionGenerator<numberType : Number>(
        private val toType: Double.() -> numberType
) : BinDistributionGenerator<
        numberType,
        FixedWidthBinCountDistributionRequest<numberType>,
        DistributionProfile<numberType, FixedWidthBinDistribution<numberType, BinWithMembers<numberType>>>,
        FixedWidthBinDistribution<numberType, BinWithMembers<numberType>>,
        BinWithMembers<numberType>> {

    override fun create(
            request: FixedWidthBinCountDistributionRequest<numberType>
    ): Result<String, DistributionProfile<numberType, FixedWidthBinDistribution<numberType, BinWithMembers<numberType>>>> {

        TODO()
    }
}