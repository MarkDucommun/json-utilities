package com.hcsc.de.claims.distributionFitting

import com.hcsc.de.claims.distributions.Randomable
import com.hcsc.de.claims.results.Result
import net.sourceforge.jdistlib.LogNormal
import net.sourceforge.jdistlib.Normal
import net.sourceforge.jdistlib.Weibull
import net.sourceforge.jdistlib.generic.GenericDistribution
import umontreal.ssj.randvar.RandomVariateGen

interface ParametricFitter {

    fun weibullParameters(list: List<Double>): Result<String, WeibullParameters>

    fun weibullDistribution(list: List<Double>): Result<String, Randomable<Double>>

    fun normalParameters(list: List<Double>): Result<String, NormalParameters>

    fun normalDistribution(list: List<Double>): Result<String, Randomable<Double>>

    fun gammaParameters(list: List<Double>): Result<String, GammaParameters>

    fun gammaDistribution(list: List<Double>): Result<String, Randomable<Double>>

    fun lognormalParameters(list: List<Double>): Result<String, LognormalParameters>

    fun lognormalDistribution(list: List<Double>): Result<String, Randomable<Double>>
}

interface DistributionHolder {
    val distribution: Randomable<Double>
}

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

data class GammaParameters(
        val shape: Double,
        val scale: Double,
        val location: Double = 0.0
): DistributionHolder {
    override val distribution: Randomable<Double> = GenericDistributionRandomable(
            genericDistribution = Weibull(shape, scale),
            shift = location
    )
}

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

data class NormalParameters(
        val mean: Double,
        val standardDeviation: Double
): DistributionHolder {
    override val distribution: Randomable<Double> = GenericDistributionRandomable(Normal(mean, standardDeviation))
}

class MontrealDistribution<genType: RandomVariateGen>(
        private val randomVariateGen: genType,
        private val shift: Double? = null
) : Randomable<Double> {

    override fun random(): Double {

        return randomVariateGen.nextDouble() + (shift ?: 0.0)
    }
}

class GenericDistributionRandomable (
        private val genericDistribution: GenericDistribution,
        private val shift: Double = 0.0
): Randomable<Double> {

    override fun random(): Double {
        return genericDistribution.random() + shift
    }
}

