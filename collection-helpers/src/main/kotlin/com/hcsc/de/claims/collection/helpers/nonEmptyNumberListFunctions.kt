package com.hcsc.de.claims.collection.helpers

val doubleToInt: Double.() -> Int = { Math.round(this).toInt() }

val doubleToLong: Double.() -> Long = { Math.round(this) }

val doubleToDouble: Double.() -> Double = Double::toDouble

fun <numberType : Number> NonEmptyList<numberType>.average(toType: Double.() -> numberType): numberType =
        all.map(Number::toDouble).average().toType()

fun NonEmptyList<Double>.average(): Double = average(toType = doubleToDouble)

fun NonEmptyList<Int>.average(): Int = average(toType = doubleToInt)

fun NonEmptyList<Long>.average(): Long = average(toType = doubleToLong)

fun <numberType : Number> NonEmptyList<numberType>.minimum(toType: Double.() -> numberType): numberType =
        all.map(Number::toDouble).min()?.toType() ?: first

fun NonEmptyList<Double>.minimum(): Double = minimum(toType = doubleToDouble)

fun NonEmptyList<Int>.minimum(): Int = minimum(toType = doubleToInt)

fun NonEmptyList<Long>.minimum(): Long = minimum(toType = doubleToLong)

fun <numberType : Number> NonEmptyList<numberType>.maximum(toType: Double.() -> numberType): numberType =
        all.map(Number::toDouble).max()?.toType() ?: first

fun NonEmptyList<Double>.maximum(): Double = maximum(toType = doubleToDouble)

fun NonEmptyList<Int>.maximum(): Int = maximum(toType = doubleToInt)

fun NonEmptyList<Long>.maximum(): Long = maximum(toType = doubleToLong)

fun <numberType : Number> NonEmptyList<numberType>.median(toType: Double.() -> numberType): numberType =

        all.map(Number::toDouble).let { allDoubles ->

            val sorted = allDoubles.sorted()
            val middleIndex = size / 2

            if (sorted.size % 2.0 == 0.0) {
                (sorted[middleIndex] + sorted[middleIndex - 1]) / 2.0
            } else {
                sorted[middleIndex]
            }.toType()
        }

fun NonEmptyList<Double>.median(): Double = median(toType = doubleToDouble)

fun NonEmptyList<Int>.median(): Int = median(toType = doubleToInt)

fun NonEmptyList<Long>.median(): Long = median(toType = doubleToLong)

fun <numberType : Number> NonEmptyList<numberType>.mode(): List<numberType> =

        all.fold(emptyMap<numberType, Int>()) { accumulator, value ->

            accumulator.plus(value to (accumulator.get(value)?.plus(1) ?: 1))

        }.let { it.toList().sortedByDescending { it.second } }.let {

            val maxCount = it.first().second

            it.filter { it.second == maxCount }

        }.map { it.first }

fun <numberType: Number> NonEmptyList<numberType>.simpleMode(
        toType: Double.() -> numberType
): numberType = mode().map(Number::toDouble).sortedByDescending { it }.first().toType()

fun NonEmptyList<Double>.simpleMode(): Double = simpleMode(toType = doubleToDouble)

fun NonEmptyList<Int>.simpleMode(): Int = simpleMode(toType = doubleToInt)

fun NonEmptyList<Long>.simpleMode(): Long = simpleMode(toType = doubleToLong)

fun <type: Comparable<type>> NonEmptyList<type>.sorted(): NonEmptyList<type> = sortBy { it }

fun <type: Comparable<type>> NonEmptyList<type>.sortedDescending(): NonEmptyList<type> = sortByDescending { it }