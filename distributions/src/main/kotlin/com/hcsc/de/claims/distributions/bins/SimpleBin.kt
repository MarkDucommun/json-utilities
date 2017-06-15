package com.hcsc.de.claims.distributions.bins

import com.hcsc.de.claims.distributions.bins.Bin

data class SimpleBin<out type : Number>(
    override val identifyingCharacteristic: type,
    override val size: Int
) : Bin<type> {

    fun incrementSize(): SimpleBin<type> = copy(size = size + 1)
}