package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.distributions.binDistributions.BinWithMembersDistribution
import com.hcsc.de.claims.distributions.bins.BinWithMembers
import com.hcsc.de.claims.distributions.generation.DistributionRequest.IdealBinCountDistributionRequest
import com.hcsc.de.claims.results.Result

class IdealBinCountDistributionGenerator<numberType : Number> : BinDistributionGenerator<
        numberType,
        IdealBinCountDistributionRequest<numberType>,
        DistributionProfile<numberType, BinWithMembersDistribution<numberType, BinWithMembers<numberType>>>,
        BinWithMembersDistribution<numberType, BinWithMembers<numberType>>,
        BinWithMembers<numberType>> {

    override fun create(
            request: IdealBinCountDistributionRequest<numberType>
    ): Result<String, DistributionProfile<numberType, BinWithMembersDistribution<numberType, BinWithMembers<numberType>>>> {
        TODO("not implemented")
    }
}