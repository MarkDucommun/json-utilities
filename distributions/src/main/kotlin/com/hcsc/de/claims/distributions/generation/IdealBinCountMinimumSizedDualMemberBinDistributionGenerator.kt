package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.distributions.Distribution
import com.hcsc.de.claims.distributions.binDistributions.BinDistribution
import com.hcsc.de.claims.distributions.bins.BinWithMembers
import com.hcsc.de.claims.distributions.bins.DualMemberBin
import com.hcsc.de.claims.distributions.generation.DistributionRequest.DualDistributionRequest.IdealBinCountDualDistributionRequest
import com.hcsc.de.claims.results.Result

class IdealBinCountMinimumSizedDualMemberBinDistributionGenerator<numberType: Number>(
        private val toType: Double.() -> numberType
) : BinDistributionGenerator<
        numberType,
        IdealBinCountDualDistributionRequest<numberType>,
        DistributionProfile<numberType, BinDistribution<numberType, DualMemberBin<numberType, BinWithMembers<numberType>>>>,
        BinDistribution<numberType, DualMemberBin<numberType, BinWithMembers<numberType>>>,
        DualMemberBin<numberType, BinWithMembers<numberType>>> {

    override fun create(
            request: IdealBinCountDualDistributionRequest<numberType>
    ): Result<String, DistributionProfile<numberType, BinDistribution<numberType, DualMemberBin<numberType, BinWithMembers<numberType>>>>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}