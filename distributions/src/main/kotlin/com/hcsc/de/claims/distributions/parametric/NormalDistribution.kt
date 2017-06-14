package com.hcsc.de.claims.distributions.parametric

import com.hcsc.de.claims.distributions.Distribution

interface NormalDistribution<out numberType : Number>: Distribution<numberType> {

    val standardDeviation: Double
}