package com.hcsc.de.claims.distributionFitting

import com.hcsc.de.claims.distributions.Randomable
import com.hcsc.de.claims.helpers.Failure
import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.Success
import com.hcsc.de.claims.helpers.map
import org.renjin.script.RenjinScriptEngineFactory
import org.renjin.sexp.DoubleArrayVector
import org.renjin.sexp.ListVector

object FitDistrPlus : ParametricFitter {

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

            val shift = list.min() ?: 0.0

            val shiftedList = list.map { it - shift + 0.00001 }

            engine.eval("library(fitdistrplus)")

            engine.put("my_data", shiftedList.toDoubleArray())

            val result = engine.eval("fitdist(my_data, distr = \"weibull\", method = \"mle\", lower = c(0, 0))") as ListVector

            val estimate = result.get("estimate") as DoubleArrayVector

            Success(WeibullParameters(shape = estimate.get(0), scale = estimate.get(1), location = shift))

        } catch (e: Exception) {

            Failure(content = e.message ?: "Something went wrong with FitDistrPlus")
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

            Failure(content = e.message ?: "Something went wrong with FitDistrPlus")
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

            Failure(content = e.message ?: "Something went wrong with FitDistrPlus")
        }
    }
}