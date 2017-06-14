package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.distributions.bins.Bin
import com.hcsc.de.claims.results.Result
import com.hcsc.de.claims.results.Success

class DoubleDataBinner: DataBinner<Double> {

    override fun bin(list: List<Double>): Result<String, List<Bin<Double>>> {

        return Success(list.unknownDistribution().bins)
    }
}