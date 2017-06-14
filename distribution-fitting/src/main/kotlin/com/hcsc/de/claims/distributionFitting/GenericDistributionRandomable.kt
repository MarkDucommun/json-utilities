package com.hcsc.de.claims.distributionFitting

import com.hcsc.de.claims.distributions.Randomable
import net.sourceforge.jdistlib.generic.GenericDistribution

class GenericDistributionRandomable (
        private val genericDistribution: GenericDistribution,
        private val shift: Double = 0.0
): Randomable<Double> {

    override fun random(): Double = genericDistribution.random() + shift
}