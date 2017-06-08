package com.hcsc.de.claims.distributionFitting

import com.hcsc.de.claims.distributions.Randomable
import com.hcsc.de.claims.helpers.Failure
import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.flatMap
import com.hcsc.de.claims.helpers.wrapExternalLibraryUsageAsResult
import umontreal.ssj.probdist.GammaDist
import umontreal.ssj.probdist.LognormalDist
import umontreal.ssj.probdist.NormalDist
import umontreal.ssj.probdist.WeibullDist
import umontreal.ssj.randvar.*
import umontreal.ssj.rng.BasicRandomStreamFactory
import umontreal.ssj.rng.F2NL607

object Montreal : ParametricFitter {

    override fun weibullParameters(list: List<Double>): Result<String, WeibullParameters> {

        return wrapMontrealAsResult {

            val (shiftedList, shift) = list.shiftTo()

            val dist = WeibullDist.getInstanceFromMLE(shiftedList.toDoubleArray(), list.size)

            WeibullParameters(shape = dist.alpha, scale = 1 / dist.lambda, location = shift)
        }
    }

    override fun weibullDistribution(list: List<Double>): Result<String, Randomable<Double>> {

        return weibullParameters(list).flatMap { randomVariateGen<WeibullGen>(it.shape, 1 / it.scale, it.location) }
    }

    override fun normalParameters(list: List<Double>): Result<String, NormalParameters> {

        return wrapMontrealAsResult {

            val dist = NormalDist.getInstanceFromMLE(list.toDoubleArray(), list.size)

            NormalParameters(mean = dist.mean, standardDeviation = dist.standardDeviation)
        }
    }

    override fun normalDistribution(list: List<Double>): Result<String, Randomable<Double>> {

        return normalParameters(list).flatMap { randomVariateGen<NormalGen>(it.mean, it.standardDeviation) }
    }

    override fun gammaParameters(list: List<Double>): Result<String, GammaParameters> {

        return wrapMontrealAsResult {

            val dist = GammaDist.getInstanceFromMLE(list.toDoubleArray(), list.size)

            GammaParameters(shape = dist.alpha, scale = 1 / dist.lambda)
        }
    }

    override fun gammaDistribution(list: List<Double>): Result<String, Randomable<Double>> {

        return gammaParameters(list).flatMap { randomVariateGen<GammaGen>(it.shape, 1 / it.scale) }
    }

    override fun lognormalParameters(list: List<Double>): Result<String, LognormalParameters> {

        return wrapMontrealAsResult {

            val dist = LognormalDist.getInstanceFromMLE(list.toDoubleArray(), list.size)

            LognormalParameters(shape = dist.mu, scale = dist.sigma)
        }
    }

    override fun lognormalDistribution(list: List<Double>): Result<String, Randomable<Double>> {
        return lognormalParameters(list).flatMap { randomVariateGen<LognormalGen>(it.shape, it.scale) }
    }

    fun <successType> wrapMontrealAsResult(fn: () -> successType): Result<String, successType> {
        return wrapExternalLibraryUsageAsResult(defaultMessage = "Montreal Java Library Failure", fn = fn)
    }

    inline fun <reified genType : RandomVariateGen> randomVariateGen(vararg args: Number): Result<String, Randomable<Double>> {

        return genType::class.constructors.find { it.parameters.size == args.size + 1 }?.let { constructor ->

            wrapExternalLibraryUsageAsResult<Randomable<Double>>(defaultMessage = "Something went wrong with reflection") {

                val stream = BasicRandomStreamFactory(F2NL607::class.java).newInstance()

                MontrealDistribution(constructor.call(stream, *args))
            }
        } ?: Failure<String, Randomable<Double>>("Could not find constructor for Montreal RandomVariateGen type for args: RandomableStream, ${args.joinToString(", ")}")
    }

    private fun List<Double>.shiftTo(value: Double = 0.0): ShiftedList<Double> {

        return this.min()?.let { min ->

            val shift = min - value

            ShiftedList(list = this.map { it - shift }, shift = min)

        } ?: ShiftedList(list = emptyList(), shift = 0.0)
    }

    data class ShiftedList<out numberType : Number>(
            val list: List<numberType>,
            val shift: numberType
    )
}