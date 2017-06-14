package com.hcsc.de.claims.distributions.binDistributions

import com.hcsc.de.claims.distributions.bins.BinWithMembers
import com.hcsc.de.claims.distributions.Distribution
import com.hcsc.de.claims.distributions.bins.DualMemberBin
import com.hcsc.de.claims.distributions.bins.Bin
import com.hcsc.de.claims.math.helpers.median
import com.hcsc.de.claims.math.helpers.mode

interface BinDistribution<out numberType : Number, out binType: Bin<numberType>> : Distribution<numberType> {

    val bins: List<binType>
}

open class BinWithMembersDistribution<numberType : Number, out binType: BinWithMembers<numberType>>(

        rawBins: List<binType>,
        toType: Double.() -> numberType

) : BinDistribution<numberType, binType> {

    override val bins = rawBins

    val members: List<Double> = rawBins.flatMap { it.members }.map(Number::toDouble)

    override fun random(): numberType {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override val average: numberType = members.average().toType()
    override val minimum: numberType = (members.min() ?: 0.0).toType()
    override val maximum: numberType = (members.max() ?: 0.0).toType()
    override val mode: numberType = members.mode().toType()
    override val median: numberType = members.median().toType()
}

interface DualMemberBinsDistribution<numberType: Number> : BinDistribution<numberType, DualMemberBin<numberType, BinWithMembers<numberType>>> {

    val asTwoDistributions: Pair<BinDistribution<numberType, BinWithMembers<numberType>>, BinDistribution<numberType, BinWithMembers<numberType>>>
}

open class AutomaticDualMemberBinsDistribution<numberType : Number>(

        rawBins: List<DualMemberBin<numberType, BinWithMembers<numberType>>>,
        val toType: Double.() -> numberType

) : BinWithMembersDistribution<numberType, DualMemberBin<numberType, BinWithMembers<numberType>>>(
        rawBins = rawBins,
        toType = toType
), DualMemberBinsDistribution<numberType> {

    override val asTwoDistributions: Pair<BinDistribution<numberType, BinWithMembers<numberType>>, BinDistribution<numberType, BinWithMembers<numberType>>>
        get() = BinWithMembersDistribution(rawBins = bins.map { it.binOne }, toType = toType) to BinWithMembersDistribution(rawBins = bins.map { it.binTwo }, toType = toType)
}

data class IntBinWithMembersDistribution(
        override val bins: List<BinWithMembers<Int>>
) : BinWithMembersDistribution<Int, BinWithMembers<Int>>(
        rawBins = bins,
        toType = Double::toInt
)