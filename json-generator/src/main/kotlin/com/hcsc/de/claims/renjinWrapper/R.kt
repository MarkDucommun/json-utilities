package com.hcsc.de.claims.renjinWrapper

import com.hcsc.de.claims.distributions.Randomable
import com.hcsc.de.claims.helpers.*
import com.sun.tools.javah.Gen
import net.sourceforge.jdistlib.Gamma
import net.sourceforge.jdistlib.LogNormal
import net.sourceforge.jdistlib.Normal
import net.sourceforge.jdistlib.Weibull
import net.sourceforge.jdistlib.generic.GenericDistribution
import org.renjin.script.RenjinScriptEngineFactory
import org.renjin.sexp.DoubleArrayVector
import org.renjin.sexp.ListVector

interface R {

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
        val scale: Double
) : DistributionHolder {
    override val distribution: Randomable<Double> = GenericDistributionRandomable(Weibull(shape, scale))
}

data class GammaParameters(
        val shape: Double,
        val scale: Double
): DistributionHolder {
    override val distribution: Randomable<Double> = GenericDistributionRandomable(Gamma(shape, scale))
}

data class LognormalParameters(
        val shape: Double,
        val scale: Double
): DistributionHolder {
    override val distribution: Randomable<Double> = GenericDistributionRandomable(LogNormal(shape, scale))
}

data class NormalParameters(
        val mean: Double,
        val standardDeviation: Double
): DistributionHolder {
    override val distribution: Randomable<Double> = GenericDistributionRandomable(Normal(mean, standardDeviation))
}

class GenericDistributionRandomable (
        private val genericDistribution: GenericDistribution
): Randomable<Double> {

    override fun random(): Double {
        return genericDistribution.random()
    }
}

object Renjin : R {

    override fun weibullDistribution(list: List<Double>): Result<String, Randomable<Double>> {
        return weibullParameters(list).map { it.distribution }
    }

    override fun normalDistribution(list: List<Double>): Result<String, Randomable<Double>> {
        return normalParameters(list).map { it.distribution }
    }

    override fun gammaDistribution(list: List<Double>): Result<String, Randomable<Double>> {
        return gammaParameters(list).map { it.distribution }
    }

    override fun lognormalDistribution(list: List<Double>): Result<String, Randomable<Double>> {
        return lognormalParameters(list).map { it.distribution }
    }

    private val engine = RenjinScriptEngineFactory().scriptEngine

    // Kotlin way of generating a normal distribution is much faster
    override fun normalParameters(list: List<Double>): Result<String, NormalParameters> {

        engine.eval("library(fitdistrplus)")

        engine.put("my_data", list.toDoubleArray())

        val result = engine.eval("fitdist(my_data, distr = \"norm\", method = \"mle\", lower = c(0, 0))") as ListVector

        val estimate = result.get("estimate") as DoubleArrayVector

        return Success(NormalParameters(mean = estimate.get(0), standardDeviation = estimate.get(1)))
    }

    override fun weibullParameters(list: List<Double>): Result<String, WeibullParameters> {

        return try {

            engine.eval("library(fitdistrplus)")

            engine.put("my_data", list.toDoubleArray())

            val result = engine.eval("fitdist(my_data, distr = \"weibull\", method = \"mle\", lower = c(0, 0))") as ListVector

            val estimate = result.get("estimate") as DoubleArrayVector

            Success(WeibullParameters(shape = estimate.get(0), scale = estimate.get(1)))

        } catch (e: Exception) {

            Failure(content = e.message ?: "Something went wrong with Renjin")
        }
    }

    override fun gammaParameters(list: List<Double>): Result<String, GammaParameters> {

        return try {

            engine.eval("library(fitdistrplus)")

            engine.put("my_data", list.toDoubleArray())

            val result = engine.eval("fitdist(my_data, distr = \"gamma\", method = \"mle\", lower = c(0, 0))") as ListVector

            val estimate = result.get("estimate") as DoubleArrayVector

            Success(GammaParameters(shape = estimate.get(0), scale = 1 / estimate.get(1)))

        } catch (e: Exception) {

            Failure(content = e.message ?: "Something went wrong with Renjin")
        }
    }

    override fun lognormalParameters(list: List<Double>): Result<String, LognormalParameters> {

        return try {

            engine.eval("library(fitdistrplus)")

            engine.put("my_data", list.toDoubleArray())

            val result = engine.eval("fitdist(my_data, distr = \"lnorm\", method = \"mme\", lower = c(0, 0))") as ListVector

            val estimate = result.get("estimate") as DoubleArrayVector

            Success(LognormalParameters(shape = estimate.get(0), scale = 1 / estimate.get(1)))

        } catch (e: Exception) {

            Failure(content = e.message ?: "Something went wrong with Renjin")
        }
    }
}