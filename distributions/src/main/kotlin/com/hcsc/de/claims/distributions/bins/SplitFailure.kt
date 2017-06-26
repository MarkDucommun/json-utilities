package com.hcsc.de.claims.distributions.bins

sealed class SplitFailure

object NotEnoughMembersToSplitFailure : SplitFailure()

object SingleMemberValueSplitFailure : SplitFailure()

object UpperSplitFailure : SplitFailure()

object LowerSplitFailure : SplitFailure()