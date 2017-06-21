package com.hcsc.de.claims.distributions

import com.hcsc.de.claims.distributions.bins.BinWithMembers
import com.hcsc.de.claims.distributions.bins.DualSourceBinWithMembers

data class SplitHolder(
        val splitBin: DualSourceBinWithMembers<Double, BinWithMembers<Double>>? = null,
        val newBins: List<DualSourceBinWithMembers<Double, BinWithMembers<Double>>> = emptyList()
){

    val binWasSplit: Boolean = splitBin != null

    fun runIfNoBinWasSplitYet(fn: () -> SplitHolder): SplitHolder =
            if (binWasSplit) this else fn.invoke()
}