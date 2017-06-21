package com.hcsc.de.claims.distributions.bins

import com.hcsc.de.claims.results.Result

interface DualSourceBinWithMembers<memberType : Number, binType : BinWithMembers<memberType>> : BinWithMembers<memberType> {

    val sourceOneBin: binType
    val sourceTwoBin: binType

    fun plus(other: DualSourceBinWithMembers<memberType, binType>): DualSourceBinWithMembers<memberType, binType>

    fun splitDualSourceBin(minimumSourceBinSize: Int, splitPoint: memberType): Result<DualSourceSplitFailure, SplitBinHolder<memberType, DualSourceBinWithMembers<memberType, binType>>>
}