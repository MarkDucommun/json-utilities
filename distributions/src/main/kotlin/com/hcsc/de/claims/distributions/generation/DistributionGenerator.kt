package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.distributions.Distribution
import com.hcsc.de.claims.results.Result


interface DistributionGenerator<
        numberType: Number,
        resultType: DistributionProfile<numberType, distributionType>,
        distributionType: Distribution<numberType>> {

    fun profile(list: List<numberType>): Result<String, resultType>
}