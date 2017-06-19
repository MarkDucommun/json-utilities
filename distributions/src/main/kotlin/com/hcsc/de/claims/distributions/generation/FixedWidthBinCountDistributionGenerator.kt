package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.distributions.binDistributions.FixedWidthBinDistribution
import com.hcsc.de.claims.distributions.bins.Bin
import com.hcsc.de.claims.distributions.generation.DistributionRequest.FixedWidthBinCountDistributionRequest
import com.hcsc.de.claims.results.Result
import com.hcsc.de.claims.results.map

class FixedWidthBinCountDistributionGenerator<numberType : Number>(
        private val toType: Double.() -> numberType
) : BinDistributionGenerator<
        numberType,
        FixedWidthBinCountDistributionRequest<numberType>,
        DistributionProfile<numberType, FixedWidthBinDistribution<numberType, Bin<numberType>>>,
        FixedWidthBinDistribution<numberType, Bin<numberType>>,
        Bin<numberType>> {

    override fun create(
            request: FixedWidthBinCountDistributionRequest<numberType>
    ): Result<String, DistributionProfile<numberType, FixedWidthBinDistribution<numberType, Bin<numberType>>>> {

        return request.list.genericFixedWidthBinCountDistribution(
                binCount = request.binCount,
                rangeMaximum = request.maximum,
                rangeMinimum = request.minimum,
                toType = toType
        ).map { DistributionProfile(pValue = 1.0, distribution = it) }
    }
}