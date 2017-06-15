package com.hcsc.de.claims.distributions.binDistributions

import com.hcsc.de.claims.distributions.Distribution
import com.hcsc.de.claims.distributions.bins.Bin

interface BinDistribution<out numberType : Number, out binType: Bin<numberType>> : Distribution<numberType> {

    val bins: List<binType>

    val binCount: Int
}