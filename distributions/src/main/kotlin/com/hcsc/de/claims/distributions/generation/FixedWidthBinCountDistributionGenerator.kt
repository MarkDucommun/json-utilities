package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.distributions.generation.DistributionRequest.FixedWidthBinCountDistributionRequest
import com.hcsc.de.claims.results.Failure
import com.hcsc.de.claims.results.Result
import com.hcsc.de.claims.results.Success

class FixedWidthBinCountDistributionGenerator<numberType : Number>(
        private val toType: Double.() -> numberType
) : BinDistributionGenerator<numberType, FixedWidthBinCountDistributionRequest<numberType>> {

    override fun create(
            request: FixedWidthBinCountDistributionRequest<numberType>
    ): Result<String, DistributionProfile<numberType>> {

        val (list, binWidth) = request

        return if (list.isNotEmpty()) {

            Success(DistributionProfile(
                    pValue = 1.0,
                    distribution = list.genericFixedWidthBinDistribution(
                            binCount = request.binCount,
                            rangeMaximum = null,
                            rangeMinimum = null,
                            toType = toType
                    )))
        } else {
            Failure("")
        }
    }
}