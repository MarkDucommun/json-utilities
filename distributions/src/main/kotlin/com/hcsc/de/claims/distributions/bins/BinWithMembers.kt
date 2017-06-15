package com.hcsc.de.claims.distributions.bins

import com.hcsc.de.claims.distributions.Distribution

interface BinWithMembers<memberType : Number> : BinWithWidth<memberType>, Distribution<memberType> {

    val members: List<memberType>

    val doubleMembers: List<Double>

    fun plus(other: BinWithMembers<memberType>): BinWithMembers<memberType>

    fun split(value: memberType): SplitBinHolder<memberType, BinWithMembers<memberType>>

    fun splitByDouble(value: Double): SplitBinHolder<memberType, BinWithMembers<memberType>>
}