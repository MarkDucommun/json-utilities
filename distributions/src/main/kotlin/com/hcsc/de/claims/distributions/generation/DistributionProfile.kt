package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.distributions.Distribution

data class DistributionProfile<out numberType: Number, out distributionType: Distribution<numberType>>(
        val pValue: Double,
        val distribution: distributionType
)