package com.hcsc.de.claims.distributionFitting

import com.hcsc.de.claims.distributions.Randomable
import net.sourceforge.jdistlib.LogNormal

data class LognormalParameters(
        val shape: Double,
        val scale: Double,
        val location: Double = 0.0
): DistributionHolder {

    override val distribution: Randomable<Double> = GenericDistributionRandomable(
            genericDistribution = LogNormal(shape, scale),
            shift = location
    )
}