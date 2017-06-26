package com.hcsc.de.claims.distributionFitting

import com.hcsc.de.claims.distributions.DoubleWithinZeroAndOne
import com.hcsc.de.claims.distributions.Randomable

data class RandomableProfile<out numberType: Number>(
        val randomable: Randomable<numberType>,
        val pValue: DoubleWithinZeroAndOne
)