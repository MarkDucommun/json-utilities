package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.distributions.bins.Bin
import com.hcsc.de.claims.results.Result

interface DataBinner<numberType: Number> {

    fun bin(list: List<numberType>): Result<String, List<Bin<numberType>>>
}