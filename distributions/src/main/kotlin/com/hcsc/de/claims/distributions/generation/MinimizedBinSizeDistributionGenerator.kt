package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.distributions.binDistributions.BinDistribution
import com.hcsc.de.claims.distributions.bins.BinWithMembers
import com.hcsc.de.claims.distributions.generation.DistributionRequest.MinimizeBinSizeDistributionRequest
import com.hcsc.de.claims.results.Result
import com.hcsc.de.claims.results.Success
import com.hcsc.de.claims.results.map

open class MinimizedBinSizeDistributionGenerator<numberType : Number>(
        private val toType: Double.() -> numberType
) : BinDistributionGenerator<
        numberType,
        MinimizeBinSizeDistributionRequest<numberType>,
        DistributionProfile<numberType, BinDistribution<numberType, BinWithMembers<numberType>>>,
        BinDistribution<numberType, BinWithMembers<numberType>>,
        BinWithMembers<numberType>> {

    override fun create(
            request: MinimizeBinSizeDistributionRequest<numberType>
    ): Result<String, DistributionProfile<numberType, BinDistribution<numberType, BinWithMembers<numberType>>>> =
            request.list.genericMinimizedBinSizeBinDistribution(
                    minimumBinSize = request.binSize,
                    rangeMinimum = null,
                    rangeMaximum = null,
                    toType = toType
            ).map { DistributionProfile(pValue = 1.0, distribution = it) }
}