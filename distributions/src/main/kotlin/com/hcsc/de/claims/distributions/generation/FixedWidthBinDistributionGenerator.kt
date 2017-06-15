package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.distributions.generation.DistributionRequest.FixedWidthBinDistributionRequest
import com.hcsc.de.claims.results.Failure
import com.hcsc.de.claims.results.Result

class FixedWidthBinDistributionGenerator<numberType: Number>(
        private val toType: Double.() -> numberType
) : BinDistributionGenerator<numberType, FixedWidthBinDistributionRequest<numberType>> {

    override fun create(
            request: FixedWidthBinDistributionRequest<numberType>
    ): Result<String, DistributionProfile<numberType>> {

        val (list, binWidth) = request

        return if (list.isNotEmpty()) {

//            list.genericFixedWidthBinDistribution(
//                    binCount = r
//            )

            TODO()

        } else {
            Failure("")
        }
    }
}