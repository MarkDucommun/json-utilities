package com.hcsc.de.claims.distributionFitting

import com.hcsc.de.claims.distributions.Randomable
import umontreal.ssj.randvar.RandomVariateGen

class MontrealRandomable<genType: RandomVariateGen>(
        private val randomVariateGen: genType,
        private val shift: Double? = null
) : Randomable<Double> {

    override fun random(): Double = randomVariateGen.nextDouble() + (shift ?: 0.0)
}