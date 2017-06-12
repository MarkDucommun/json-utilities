package com.hcsc.de.claims.distributions

import com.hcsc.de.claims.helpers.Failure
import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.Success

class DoubleWithinZeroAndOne private constructor(
        val value: Double
) {

    companion object {

        fun create(value: Double): Result<String, DoubleWithinZeroAndOne> =
                if (value > 1.0 || value < 0.0)
                    Failure("Invalid - value out of bounds")
                else
                    Success(DoubleWithinZeroAndOne(value))
    }

    override fun equals(other: Any?): Boolean = when (other) {
        is DoubleWithinZeroAndOne -> value == other.value
        else -> false
    }
}