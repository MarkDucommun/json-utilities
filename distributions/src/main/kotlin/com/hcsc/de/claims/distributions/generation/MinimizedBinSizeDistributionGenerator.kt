package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.distributions.binDistributions.BinDistribution
import com.hcsc.de.claims.distributions.bins.BinWithMembers
import com.hcsc.de.claims.distributions.generation.DistributionRequest.MinimizeBinSizeDistributionRequest
import com.hcsc.de.claims.results.Result
import com.hcsc.de.claims.results.Success

open class MinimizedBinSizeDistributionGenerator<numberType : Number>(
        private val toBinNumberType: BinWithMembers<Double>.() -> BinWithMembers<numberType>,
        private val toBinDistribution: List<BinWithMembers<numberType>>.() -> BinDistribution<numberType, BinWithMembers<numberType>>
) : BinDistributionGenerator<
        numberType,
        MinimizeBinSizeDistributionRequest<numberType>,
        DistributionProfile<numberType, BinDistribution<numberType, BinWithMembers<numberType>>>,
        BinDistribution<numberType, BinWithMembers<numberType>>,
        BinWithMembers<numberType>> {

    override fun create(
            request: MinimizeBinSizeDistributionRequest<numberType>
    ): Result<String, DistributionProfile<numberType, BinDistribution<numberType, BinWithMembers<numberType>>>> =
            Success(DistributionProfile(
                    pValue = 1.0,
                    distribution = request.list.genericMinimizedBinSizeBinDistribution(
                            minimumBinSize = request.binSize,
                            rangeMinimum = null,
                            rangeMaximum = null,
                            toBinNumberType = toBinNumberType,
                            toBinDistribution = toBinDistribution
                    )))
}