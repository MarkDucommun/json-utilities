package com.hcsc.de.claims.distributions

interface Randomable<out numberType: Number> {

    fun random() : numberType
}