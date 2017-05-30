package com.hcsc.de.claims.renjinWrapper

import com.hcsc.de.claims.helpers.Failure
import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.Success
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation
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
        val standardDeviation: StandardDeviation
)

object Renjin : R {

    private val engine = RenjinScriptEngineFactory().scriptEngine

    override fun normalParameters(list: List<Double>): Result<String, > {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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