package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.distributions.binDistributions.BinDistribution
import com.hcsc.de.claims.distributions.bins.Bin
import com.hcsc.de.claims.results.Result

interface BinDistributionGenerator<
        out numberType: Number,
        in requestType: DistributionRequest<numberType>,
        responseType: DistributionProfile<numberType, binDistributionType>,
        out binDistributionType: BinDistribution<numberType, binType>,
        out binType: Bin<numberType>> {

   fun create(request: requestType): Result<String, responseType>
}