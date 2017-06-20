package com.hcsc.de.claims.distributions

import com.hcsc.de.claims.collection.helpers.NonEmptyList

data class DistributionPair<out numberType : Number>(
        val one: List<numberType>,
        val two: List<numberType>
)

data class NonEmptyDistributionPair<numberType: Number>(
        val one: NonEmptyList<numberType>,
        val two: NonEmptyList<numberType>
)