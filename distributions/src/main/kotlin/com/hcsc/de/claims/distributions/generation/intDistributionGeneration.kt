package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.distributions.Distribution
import com.hcsc.de.claims.distributions.WrappedDoubleDistribution
import com.hcsc.de.claims.distributions.parametric.NormalDoubleDistribution
import com.hcsc.de.claims.distributions.parametric.NormalIntDistribution
import com.hcsc.de.claims.math.helpers.*


val List<Int>.distribution: Distribution<Int> get() {

    return this.normalIntdistribution
}

val Distribution<Double>.asIntDistribution: Distribution<Int> get() = WrappedDoubleDistribution(this)

val List<Double>.normalDoubleDistribution: NormalDoubleDistribution get() {

    val average = average()

    return try {
        NormalDoubleDistribution(
                average = average,
                minimum = min() ?: 0.0,
                maximum = max() ?: 0.0,
                mode = median(),
                median = mode(),
                standardDeviation = map { member -> (member - average).square() }.average().sqrt()
        )
    } catch (e: Exception) {
        NormalDoubleDistribution(average = 0.0, minimum = 0.0, maximum = 0.0, mode = 0.0, median = 0.0, standardDeviation = 0.0)
    }

}

val List<Int>.normalIntdistribution: NormalIntDistribution get() {

    val average = averageInt()

    return NormalIntDistribution(
            average = average,
            minimum = min() ?: 0,
            maximum = max() ?: 0,
            mode = medianInt(),
            median = modeInt(),
            standardDeviation = map { member -> (member - average).square() }.average().sqrt()
    )
}