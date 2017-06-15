package com.hcsc.de.claims.distributions.binDistributions

import com.hcsc.de.claims.distributions.bins.BinWithMembers
import com.hcsc.de.claims.distributions.bins.DualMemberBin


data class UnknownDualMemberVariableBinWidthDistribution<numberType : Number>(
        override val bins: List<DualMemberBin<numberType, BinWithMembers<numberType>>>,
        override val average: numberType,
        override val minimum: numberType,
        override val maximum: numberType,
        override val mode: numberType,
        override val median: numberType
) : BinDistribution<numberType, DualMemberBin<numberType, BinWithMembers<numberType>>> {

    val numberOfBins: Int = bins.size

    override fun random(): numberType {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}