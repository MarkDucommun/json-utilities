package com.hcsc.de.claims.collection.helpers

import com.hcsc.de.claims.results.Failure
import com.hcsc.de.claims.results.Result
import com.hcsc.de.claims.results.asSuccess


interface NonEmptyList<type> : List<type> {

    val first: type

    val last: type

    val all: List<type>

    fun safeGet(index: Int): Result<String, type>

    fun getOrNull(index: Int): type?

    fun plus(element: type): NonEmptyList<type>

    fun plus(elements: NonEmptyList<type>): NonEmptyList<type>

    fun plus(elements: Iterable<type>): NonEmptyList<type>

    fun drop(n: Int): Result<String, NonEmptyList<type>>

    fun dropLast(n: Int): Result<String, NonEmptyList<type>>

    fun safeDrop(n: Int): List<type>

    fun safeDropLast(n: Int): List<type>

    fun find(fn: (type) -> Boolean): Result<String, type>

    fun asReversed(): NonEmptyList<type>

    fun <sortableType: Comparable<sortableType>> sortBy(fn: (type) -> sortableType): NonEmptyList<type>

    fun <sortableType: Comparable<sortableType>> sortByDescending(fn: (type) -> sortableType): NonEmptyList<type>

    fun forEach(fn: (type) -> Unit)

    fun <newType> map(fn: (type) -> newType): NonEmptyList<newType>

    fun <newType> mapIndexed(fn: (Int, type) -> newType): NonEmptyList<newType>

    fun filter(fn: (type) -> Boolean): Result<String, NonEmptyList<type>>

    fun filterNot(fn: (type) -> Boolean): Result<String, NonEmptyList<type>>

    fun reduce(fn: (accumulator: type, value: type) -> type): type

    fun <accumulatorType> fold(accumulator: accumulatorType, fn: (accumulator: accumulatorType, value: type) -> accumulatorType): accumulatorType

    fun <otherType> zip(other: NonEmptyList<otherType>): NonEmptyList<Pair<type, otherType>>
}

