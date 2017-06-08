package com.hcsc.de.claims.renjinWrapper

import com.hcsc.de.claims.distributions.Randomable
import com.hcsc.de.claims.helpers.*
import net.sourceforge.jdistlib.Gamma
import net.sourceforge.jdistlib.LogNormal
import net.sourceforge.jdistlib.Normal
import net.sourceforge.jdistlib.Weibull
import net.sourceforge.jdistlib.generic.GenericDistribution
import flanagan.analysis.Regression
import org.renjin.script.RenjinScriptEngineFactory
import org.renjin.sexp.DoubleArrayVector
import org.renjin.sexp.ListVector

interface ParametricFitter {

    //Distributions "beta", "cauchy", "chi-squared", "exponential", "f", "gamma", "geometric",
    // "log-normal", "lognormal", "logistic", "negative binomial", "normal",
    // "Poisson", "t" and "weibull" are recognised, case being ignored.

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

object Flanagan : ParametricFitter {

    override fun weibullParameters(list: List<Double>): Result<String, WeibullParameters> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun weibullDistribution(list: List<Double>): Result<String, Randomable<Double>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun normalParameters(list: List<Double>): Result<String, NormalParameters> {
        Regression()

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun normalDistribution(list: List<Double>): Result<String, Randomable<Double>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun gammaParameters(list: List<Double>): Result<String, GammaParameters> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun gammaDistribution(list: List<Double>): Result<String, Randomable<Double>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun lognormalParameters(list: List<Double>): Result<String, LognormalParameters> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun lognormalDistribution(list: List<Double>): Result<String, Randomable<Double>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

object Renjin : ParametricFitter {

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