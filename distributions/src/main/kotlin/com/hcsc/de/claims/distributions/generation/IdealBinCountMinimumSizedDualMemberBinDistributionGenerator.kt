package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.distributions.DistributionPair
import com.hcsc.de.claims.distributions.binDistributions.DualMemberBinsDistribution
import com.hcsc.de.claims.distributions.bins.BinWithMembers
import com.hcsc.de.claims.distributions.bins.DualSourceBinWithMembers
import com.hcsc.de.claims.distributions.generation.DistributionRequest.DualDistributionRequest.IdealBinCountDualDistributionRequest
import com.hcsc.de.claims.results.Result
import com.hcsc.de.claims.results.map

class IdealBinCountMinimumSizedDualMemberBinDistributionGenerator<numberType: Number>(
        private val toType: Double.() -> numberType
) : BinDistributionGenerator<
        numberType,
        IdealBinCountDualDistributionRequest<numberType>,
        DistributionProfile<numberType, DualMemberBinsDistribution<numberType>>,
        DualMemberBinsDistribution<numberType>,
        DualSourceBinWithMembers<numberType, BinWithMembers<numberType>>> {

    override fun create(
            request: IdealBinCountDualDistributionRequest<numberType>
    ): Result<String, DistributionProfile<numberType, DualMemberBinsDistribution<numberType>>> {

        return DistributionPair(one = request.list, two = request.listTwo)
                .idealBinCountMinimumSizedDualSourceBinWithMembersDistribution(toType = toType)
                .map { DistributionProfile(pValue = 1.0, distribution = it) }
    }
}