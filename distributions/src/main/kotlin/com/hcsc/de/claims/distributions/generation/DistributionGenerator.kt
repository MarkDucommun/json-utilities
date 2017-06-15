package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.results.Result


interface DistributionGenerator<numberType: Number> {

    fun profile(list: List<numberType>): Result<String, DistributionProfile<numberType>>
}