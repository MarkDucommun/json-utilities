package com.hcsc.de.claims.distributions

import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.map

data class RatioProbability(val ratio: DoubleWithinZeroAndOne): Probability {

    override fun nextChance(): Boolean = Math.random() < ratio.value

    companion object {

        fun create(value: Double): Result<String, RatioProbability> {

            return DoubleWithinZeroAndOne.create(value).map {
                RatioProbability(it)
            }
        }
    }
}