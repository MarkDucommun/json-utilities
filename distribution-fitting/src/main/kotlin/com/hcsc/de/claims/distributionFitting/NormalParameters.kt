package com.hcsc.de.claims.distributionFitting

import com.hcsc.de.claims.distributions.Randomable
import net.sourceforge.jdistlib.Normal

data class NormalParameters(
        val mean: Double,
        val standardDeviation: Double
): DistributionHolder {

    override val distribution: Randomable<Double> = GenericDistributionRandomable(Normal(mean, standardDeviation))
}