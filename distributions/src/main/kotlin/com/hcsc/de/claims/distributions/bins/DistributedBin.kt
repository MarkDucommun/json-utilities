package com.hcsc.de.claims.distributions.bins

import com.hcsc.de.claims.distributions.Distribution
import com.hcsc.de.claims.distributions.Randomable
import com.hcsc.de.claims.distributions.bins.Bin

class DistributedBin<out numberType : Number>(
        val startValue: numberType,
        val pValue: Double,
        private val distribution: Distribution<numberType>,
        override val size: Int
) : Bin<numberType>, Randomable<numberType> {

    override val identifyingCharacteristic: numberType = startValue

    override fun random(): numberType {
        return distribution.random()
    }
}