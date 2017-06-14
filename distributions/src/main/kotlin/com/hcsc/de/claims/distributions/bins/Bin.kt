package com.hcsc.de.claims.distributions.bins

interface Bin<out type: Number> {

    val identifyingCharacteristic: type

    val size: Int
}