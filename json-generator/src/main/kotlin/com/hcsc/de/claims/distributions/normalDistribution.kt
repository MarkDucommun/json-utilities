package com.hcsc.de.claims.distributions

import com.hcsc.de.claims.helpers.*
import java.util.*

interface NormalDistribution<out numberType : Number>: Distribution<numberType> {
    val standardDeviation: Double
}

data class NormalIntDistribution(
        override val average: Int,
        override val minimum: Int,
        override val maximum: Int,
        override val mode: Int,
        override val median: Int,
        override val standardDeviation: Double
) : NormalDistribution<Int> {

    private val random = Random()

    override fun random(): Int {
        return (random.nextGaussian() * standardDeviation + average).ceilingOnEven().toInt()
    }
}

data class NormalDoubleDistribution(
        override val average: Double,
        override val minimum: Double,
        override val maximum: Double,
        override val mode: Double,
        override val median: Double,
        override val standardDeviation: Double
) : NormalDistribution<Double> {

    private val random = Random()

    override fun random(): Double {
        return random.nextGaussian() * standardDeviation + average
    }
}

val List<Double>.normalDoubleDistribution: NormalDoubleDistribution get() {

    val average = average()

    return NormalDoubleDistribution(
            average = average,
            minimum = min() ?: 0.0,
            maximum = max() ?: 0.0,
            mode = median(),
            median = mode(),
            standardDeviation = map { member -> (member - average).square() }.average().sqrt()
    )
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