package com.hcsc.de.claims.distributions.bins

import com.hcsc.de.claims.collection.helpers.NonEmptyList
import com.hcsc.de.claims.distributions.Distribution
import com.hcsc.de.claims.results.Result

interface BinWithMembers<memberType : Number> : BinWithWidth<memberType>, Distribution<memberType> {

    val members: NonEmptyList<memberType>

    fun plus(other: BinWithMembers<memberType>): BinWithMembers<memberType>

    fun split(splitPoint: memberType, minimumBinSize: Int = 1): Result<SplitFailure, SplitBinHolder<memberType, BinWithMembers<memberType>>>
}