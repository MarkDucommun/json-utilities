package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.collection.helpers.*
import com.hcsc.de.claims.distributions.binDistributions.BinDistribution
import com.hcsc.de.claims.distributions.binDistributions.BinWithMembersDistribution
import com.hcsc.de.claims.distributions.bins.AutomaticBinWithMembers
import com.hcsc.de.claims.distributions.bins.BinWithMembers
import com.hcsc.de.claims.distributions.bins.DoubleBinWithMembers
import com.hcsc.de.claims.distributions.bins.SplitBinHolder
import com.hcsc.de.claims.results.*

fun <numberType : Number> List<numberType>.genericMinimizedBinSizeBinDistribution(
        minimumBinSize: Int = 5,
        rangeMinimum: numberType? = null,
        rangeMaximum: numberType? = null,
        toType: Double.() -> numberType
): Result<String, BinDistribution<numberType, BinWithMembers<numberType>>> =
        this
                .map(Number::toDouble)
                .sorted()
                .filterNotLessThan(rangeMinimum?.toDouble())
                .filterNotGreaterThan(rangeMaximum?.toDouble())
                .asNonEmptyList()
                .map { it.map(toType) }
                .map { it.genericMinimizedBinSizeBinDistribution(minimumBinSize = minimumBinSize, toType = toType) }

fun <numberType : Number> NonEmptyList<numberType>.genericMinimizedBinSizeBinDistribution(
        minimumBinSize: Int,
        toType: Double.() -> numberType
): BinDistribution<numberType, BinWithMembers<numberType>> =
        this
                .map(Number::toDouble)
                .sorted()
                .asDoubleBin
                .maximizeBins(minimumBinSize)
                .map { AutomaticBinWithMembers(rawMembers = it.members.map(toType), toType = toType) }
                .let { BinWithMembersDistribution(rawBins = it, toType = toType) }

private fun BinWithMembers<Double>.maximizeBins(binCount: Int): List<BinWithMembers<Double>> =
        this
                .split(members.average())
                .mapError { "" /* TODO */ }
                .flatMap { it.bothAreValid(binCount) }
                .map { (lower, upper) -> listOf(lower.maximizeBins(binCount), upper.maximizeBins(binCount)).flatten() }
                .getOrElse(alternate = listOf(this))

private fun SplitBinHolder<Double, BinWithMembers<Double>>.bothAreValid(binCount: Int):
        Result<String, SplitBinHolder<Double, BinWithMembers<Double>>> =
        if (lower.isValid(binCount) && upper.isValid(binCount)) {
            Success(this)
        } else {
            Failure("")
        }

private val NonEmptyList<Double>.asDoubleBin: BinWithMembers<Double> get() = DoubleBinWithMembers(members = this)

private fun BinWithMembers<Double>.isValid(binCount: Int): Boolean = endValue - startValue >= 0.0 && size >= binCount