package com.hcsc.de.claims.collection.helpers

import com.hcsc.de.claims.results.Failure
import com.hcsc.de.claims.results.Result
import com.hcsc.de.claims.results.Success
import com.hcsc.de.claims.results.asSuccess

open class SimpleNonEmptyList<type>(
        first: type,
        private val remaining: List<type> = emptyList()
) : NonEmptyList<type>, List<type> by listOf(first).plus(remaining) {

    override val first: type = first

    override val last: type = if (remaining.isNotEmpty()) remaining.last() else first

    override val size: Int get() = remaining.size + 1

    override val all: List<type> = listOf(first).plus(remaining)

    override fun safeGet(index: Int): Result<String, type> =
            getOrNull(index)?.asSuccess<String, type>() ?: Failure("No value exists at this index")

    override fun getOrNull(index: Int): type? = listOf(first()).plus(remaining).getOrNull(index)

    override fun plus(element: type): NonEmptyList<type> =
            SimpleNonEmptyList(first = first, remaining = remaining.plus(element))

    override fun plus(elements: NonEmptyList<type>): NonEmptyList<type> =
            SimpleNonEmptyList(first = first, remaining = remaining.plus(elements.all))

    override fun plus(elements: Iterable<type>): NonEmptyList<type> =
            SimpleNonEmptyList(first = first, remaining = remaining.plus(elements))

    override fun drop(n: Int): Result<String, NonEmptyList<type>> =
            all.drop(n).asNonEmptyList()

    override fun dropLast(n: Int): Result<String, NonEmptyList<type>> = all.dropLast(n).asNonEmptyList()

    override fun safeDrop(n: Int): List<type> = all.drop(n)

    override fun safeDropLast(n: Int): List<type> = all.dropLast(n)

    override fun contains(element: type): Boolean = find { it == element } is Success

    override fun find(fn: (type) -> Boolean): Result<String, type> =
            all.find(fn)?.asSuccess() ?: Failure("Element meeting criteria was not found")

    override fun asReversed(): NonEmptyList<type> =
            remaining.lastOrNull()?.let { last ->
                SimpleNonEmptyList(first = last, remaining = remaining.dropLast(1).asReversed().plus(first))
            } ?: SimpleNonEmptyList(first = first)

    override fun <sortableType : Comparable<sortableType>> sortBy(fn: (type) -> sortableType): NonEmptyList<type> {

        val sorted = all.sortedBy(fn)

        return SimpleNonEmptyList(first = sorted.firstOrNull() ?: first, remaining = sorted.drop(1))
    }

    override fun <sortableType : Comparable<sortableType>> sortByDescending(fn: (type) -> sortableType): NonEmptyList<type> {

        val sorted = all.sortedByDescending(fn)

        return SimpleNonEmptyList(first = sorted.first() ?: first, remaining = sorted.drop(1))
    }

    override fun forEach(fn: (type) -> Unit) {
        all.forEach(fn)
    }

    override fun <newType> map(fn: (type) -> newType): NonEmptyList<newType> =
            SimpleNonEmptyList(first = fn(first), remaining = remaining.map(fn))

    override fun <newType> mapIndexed(fn: (Int, type) -> newType): NonEmptyList<newType> =
            SimpleNonEmptyList(first = fn(0, first), remaining = remaining.mapIndexed { index, value -> fn(index + 1, value) })

    override fun filter(fn: (type) -> Boolean): Result<String, NonEmptyList<type>> =
            all.filter(fn).asNonEmptyList()

    override fun filterNot(fn: (type) -> Boolean): Result<String, NonEmptyList<type>> =
            all.filterNot(fn).asNonEmptyList()

    override fun reduce(fn: (accumulator: type, value: type) -> type): type = all.reduce(fn)

    override fun <accumulatorType> fold(
            accumulator: accumulatorType,
            fn: (accumulatorType, type) -> accumulatorType
    ): accumulatorType =
            all.fold(accumulator, fn)


    override fun <otherType> zip(other: NonEmptyList<otherType>): NonEmptyList<Pair<type, otherType>> =
            SimpleNonEmptyList(
                    first = first to other.first,
                    remaining = remaining
                            .mapIndexed { index, value -> other.getOrNull(index + 1)?.let { value to it } }
                            .filterNotNull()
            )

    override fun equals(other: Any?): Boolean =
            when (other) {
                is NonEmptyList<*> -> all == other.all
                is List<*> -> all == other
                else -> false
            }

    // TODO why is first considered nullable here?
    override fun hashCode(): Int = remaining.hashCode().let { 31 * it + (first!!.hashCode() ?: 0) }
}