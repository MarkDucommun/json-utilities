package com.hcsc.de.claims.distributions


interface BinDistribution<out numberType: Number> : Distribution<numberType> {
    val bins: List<Bin>
}

interface Bin {
    val count: Int
}
