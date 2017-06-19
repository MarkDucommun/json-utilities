package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.distributions.bins.Bin
import com.hcsc.de.claims.results.Result
import com.hcsc.de.claims.results.Success
import com.hcsc.de.claims.results.map

class DoubleDataBinner: DataBinner<Double> {

    override fun bin(list: List<Double>): Result<String, List<Bin<Double>>> {

        return list.toFixedWidthBinCountDistribution().map { it.bins }
    }
}