package com.hcsc.de.claims.distributions.bins

interface DualMemberBin<memberType : Number,
        binType : BinWithMembers<memberType>> : BinWithMembers<memberType> {

    val binOne: binType

    val binTwo: binType

    fun plus(other: DualMemberBin<memberType, binType>): DualMemberBin<memberType, binType>

    override fun split(value: memberType):
            SplitBinHolder<memberType, DualMemberBin<memberType, BinWithMembers<memberType>>>

    override fun splitByDouble(value: Double):
            SplitBinHolder<memberType, DualMemberBin<memberType, BinWithMembers<memberType>>>
}