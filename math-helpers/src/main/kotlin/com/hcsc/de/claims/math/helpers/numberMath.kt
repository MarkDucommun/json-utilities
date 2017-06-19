package com.hcsc.de.claims.math.helpers

fun <T> List<T>.average(fn: T.() -> Int): Int = map(fn).averageInt()

inline fun <startType: Number, reified endType: Number> startType.asType(): endType = when (this) {
    is Byte -> when (endType::class.simpleName) {
        "Byte" -> this
        "Short" -> this.toShort()
        "Int" -> this.toInt()
        "Long" -> this.toLong()
        "Float" -> this.toFloat()
        "Double" -> this.toDouble()
        else -> TODO()
    }
    is Short -> when (endType::class.simpleName) {
        "Byte" -> this.toByte()
        "Short" -> this
        "Int" -> this.toInt()
        "Long" -> this.toLong()
        "Float" -> this.toFloat()
        "Double" -> this.toDouble()
        else -> TODO()
    }
    is Int -> when (endType::class.simpleName) {
        "Byte" -> this.toByte()
        "Short" -> this.toShort()
        "Int" -> this
        "Long" -> this.toLong()
        "Float" -> this.toFloat()
        "Double" -> this.toDouble()
        else -> TODO()
    }
    is Long -> when (endType::class.simpleName) {
        "Byte" -> this.toByte()
        "Short" -> this.toShort()
        "Int" -> this.toInt()
        "Long" -> this
        "Float" -> this.toFloat()
        "Double" -> this.toDouble()
        else -> TODO()
    }
    is Float -> when (endType::class.simpleName) {
        "Byte" -> this.toByte()
        "Short" -> this.toShort()
        "Int" -> this.toInt()
        "Long" -> this.toLong()
        "Float" -> this
        "Double" -> this.toDouble()
        else -> TODO()
    }
    is Double -> when (endType::class.simpleName) {
        "Byte" -> Math.round(this).toByte()
        "Short" -> Math.round(this).toShort()
        "Int" -> Math.round(this).toInt()
        "Long" -> Math.round(this)
        "Float" -> this.toFloat()
        "Double" -> this
        else -> TODO()
    }
    else -> TODO()
} as endType

