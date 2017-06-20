package com.hcsc.de.claims.distributions.bins

import com.hcsc.de.claims.results.Result

interface DualMemberBin<memberType : Number, binType : BinWithMembers<memberType>> : BinWithMembers<memberType> {

    val binOne: binType

    val binTwo: binType

    fun plus(other: DualMemberBin<memberType, binType>): DualMemberBin<memberType, binType>

    fun splitDualBin(value: memberType): Result<String, SplitBinHolder<memberType, DualMemberBin<memberType, binType>>>
}