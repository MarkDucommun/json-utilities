package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.distributions.binDistributions.FixedWidthBinDistribution
import com.hcsc.de.claims.distributions.bins.Bin
import com.hcsc.de.claims.distributions.generation.DistributionRequest.FixedWidthBinDistributionRequest
import com.hcsc.de.claims.results.Failure
import com.hcsc.de.claims.results.Result
import com.hcsc.de.claims.results.Success
import com.hcsc.de.claims.results.map

class FixedWidthBinDistributionGenerator<numberType : Number>(
        private val toType: Double.() -> numberType
) : BinDistributionGenerator<
        numberType,
        FixedWidthBinDistributionRequest<numberType>,
        DistributionProfile<numberType, FixedWidthBinDistribution<numberType, Bin<numberType>>>,
        FixedWidthBinDistribution<numberType, Bin<numberType>>,
        Bin<numberType>> {

    override fun create(
            request: FixedWidthBinDistributionRequest<numberType>
    ): Result<String, DistributionProfile<numberType, FixedWidthBinDistribution<numberType, Bin<numberType>>>> {

        val (list, binWidth) = request

        return if (list.isNotEmpty()) {

            list.genericFixedWidthBinDistribution(
                    binWidth = binWidth,
                    rangeMinimum = null,
                    rangeMaximum = null,
                    toType = toType
            ).map { DistributionProfile(pValue = 1.0, distribution = it) }
        } else {
            Failure("")
        }
    }
}