package com.hcsc.de.claims.distributions.binDistributions

import com.hcsc.de.claims.distributions.bins.BinWithMembers
import com.hcsc.de.claims.math.helpers.median
import com.hcsc.de.claims.math.helpers.mode

open class BinWithMembersDistribution<numberType : Number, out binType : BinWithMembers<numberType>>(

        rawBins: List<binType>,
        toType: Double.() -> numberType

) : BinDistribution<numberType, binType> {

    override val bins = rawBins

    override val binCount: Int = rawBins.size

    val members: List<Double> = rawBins.flatMap { it.members.all }.map(Number::toDouble)

    override val average: numberType = members.average().toType()
    override val minimum: numberType = (members.min() ?: 0.0).toType()
    override val maximum: numberType = (members.max() ?: 0.0).toType()
    override val mode: numberType = members.mode().toType()
    override val median: numberType = members.median().toType()

    override fun random(): numberType {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}