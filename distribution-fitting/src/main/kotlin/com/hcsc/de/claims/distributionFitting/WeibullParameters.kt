package com.hcsc.de.claims.distributionFitting

import com.hcsc.de.claims.distributions.Randomable
import net.sourceforge.jdistlib.Weibull

data class WeibullParameters(
        val shape: Double,
        val scale: Double,
        val location: Double = 0.0
) : DistributionHolder {

    override val distribution: Randomable<Double> = GenericDistributionRandomable(
            genericDistribution = Weibull(shape, scale),
            shift = location
    )
}