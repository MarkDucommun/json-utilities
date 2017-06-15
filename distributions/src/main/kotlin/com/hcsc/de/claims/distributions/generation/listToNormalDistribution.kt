package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.distributions.Distribution
import com.hcsc.de.claims.distributions.WrappedDoubleDistribution
import com.hcsc.de.claims.distributions.parametric.DoubleNormalDistribution
import com.hcsc.de.claims.distributions.parametric.IntNormalDistribution
import com.hcsc.de.claims.math.helpers.*


val List<Int>.distribution: Distribution<Int> get() = intNormalDistribution

val Distribution<Double>.asIntDistribution: Distribution<Int> get() = WrappedDoubleDistribution(this)

val List<Double>.doubleNormalDistribution: DoubleNormalDistribution
    get() = try {
        DoubleNormalDistribution(
                average = average(),
                minimum = min() ?: 0.0,
                maximum = max() ?: 0.0,
                mode = median(),
                median = mode(),
                standardDeviation = map { member -> (member - average()).square() }.average().sqrt()
        )
    } catch (e: Exception) {
        DoubleNormalDistribution(
                average = 0.0,
                minimum = 0.0,
                maximum = 0.0,
                mode = 0.0,
                median = 0.0,
                standardDeviation = 0.0
        )
    }

val List<Int>.intNormalDistribution: IntNormalDistribution get() = IntNormalDistribution(
        average = averageInt(),
        minimum = min() ?: 0,
        maximum = max() ?: 0,
        mode = medianInt(),
        median = modeInt(),
        standardDeviation = map { member -> (member - averageInt()).square() }.average().sqrt()
)