package com.hcsc.de.claims.distributions.bins

data class SplitBinHolder<numberType: Number, out binType: BinWithMembers<numberType>>(
        val upper: binType,
        val lower: binType
)