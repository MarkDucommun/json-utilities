package com.hcsc.de.claims.distributions

import com.hcsc.de.claims.distributions.bins.BinWithMembers
import com.hcsc.de.claims.distributions.bins.DualMemberBin

data class SplitHolder(
        val binSplit: DualMemberBin<Double, BinWithMembers<Double>>? = null,
        val newBins: List<DualMemberBin<Double, BinWithMembers<Double>>> = emptyList()
){
    fun notComplete(fn: () -> SplitHolder): SplitHolder {
        return if (binSplit == null) {
            fn.invoke()
        } else {
            this
        }
    }
}