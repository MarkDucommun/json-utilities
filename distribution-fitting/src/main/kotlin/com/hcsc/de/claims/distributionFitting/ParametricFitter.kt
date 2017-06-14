package com.hcsc.de.claims.distributionFitting

import com.hcsc.de.claims.distributions.Randomable
import com.hcsc.de.claims.results.Result

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