package com.hcsc.de.claims.distributionFitting

import com.hcsc.de.claims.distributions.Randomable

interface DistributionHolder {

    val distribution: Randomable<Double>
}