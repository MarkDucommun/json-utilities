package com.hcsc.de.claims.distributions.bins

sealed class SplitFailure

object UpperSplitFailure : SplitFailure()

object LowerSplitFailure : SplitFailure()