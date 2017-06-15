package com.hcsc.de.claims.distributions.bins

open class AutomaticBinWithWidth<numberType : Number>(
        rawStartValue: numberType,
        rawEndValue: numberType,
        override val size: Int,
        val toType: Double.() -> numberType
) : BinWithWidth<numberType> {

    override val Double.asType: numberType get() = toType()

    override val identifyingCharacteristic: numberType = rawStartValue
    override val startValue: numberType = rawStartValue
    override val endValue: numberType = rawEndValue
    private val doubleStartValue: Double = rawStartValue.toDouble()
    private val doubleEndValue: Double = rawEndValue.toDouble()
    override val width: numberType = (doubleEndValue - doubleStartValue).toType()

    override fun plus(other: BinWithWidth<numberType>): BinWithWidth<numberType> {

        val newStartValue = if (doubleStartValue < other.startValue.toDouble()) startValue else other.startValue
        val newEndValue = if (doubleEndValue > other.endValue.toDouble()) endValue else other.endValue

        return AutomaticBinWithWidth(
                size = size + other.size,
                rawStartValue = newStartValue,
                rawEndValue = newEndValue,
                toType = toType
        )
    }
}