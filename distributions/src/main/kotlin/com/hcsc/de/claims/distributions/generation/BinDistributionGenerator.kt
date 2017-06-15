package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.results.Result

interface BinDistributionGenerator<numberType: Number, in requestType: DistributionRequest<numberType>> {

   fun create(request: requestType): Result<String, DistributionProfile<numberType>>
}