package com.hcsc.de.claims.distributions

import com.hcsc.de.claims.results.Result
import com.hcsc.de.claims.results.Success

interface DataBinner<numberType: Number> {

    fun bin(list: List<numberType>): Result<String, List<FixedWidthBin<numberType>>>
}

class DoubleDataBinner: DataBinner<Double> {

    override fun bin(list: List<Double>): Result<String, List<FixedWidthBin<Double>>> {

        return Success(list.unknownDistribution().bins)
    }
}