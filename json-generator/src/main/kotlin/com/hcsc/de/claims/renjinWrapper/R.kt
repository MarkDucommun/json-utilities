package com.hcsc.de.claims.renjinWrapper

import com.hcsc.de.claims.helpers.Failure
import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.Success
import org.renjin.script.RenjinScriptEngineFactory
import org.renjin.sexp.DoubleArrayVector
import org.renjin.sexp.ListVector

interface R {

    fun weibullParameters(list: List<Double>): Result<String, WeibullParameters>

    fun normalParameters(list: List<Double>): Result<String, NormalParameters>
}

data class WeibullParameters(
        val shape: Double,
        val scale: Double
)

data class NormalParameters(
        val mean: Double,
        val standardDeviation: Double
)

object Renjin : R {

    private val engine = RenjinScriptEngineFactory().scriptEngine

    override fun normalParameters(list: List<Double>): Result<String, NormalParameters> {

        engine.eval("library(fitdistrplus)")

        engine.put("my_data", list.toDoubleArray())

        val result = engine.eval("fitdist(my_data, distr = \"norm\", method = \"mle\", lower = c(0, 0))") as ListVector

        val estimate = result.get("estimate") as DoubleArrayVector

        return Success(NormalParameters(mean = estimate.get(0), standardDeviation = estimate.get(1)))
    }

    fun normalParametersTwo(list: List<Double>): Result<String, NormalParameters> {

        engine.eval("library(MASS)")

        engine.put("my_data", list.toDoubleArray())

        val result = engine.eval("fitdistr(my_data, densfun=\"normal\")") as ListVector

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
}