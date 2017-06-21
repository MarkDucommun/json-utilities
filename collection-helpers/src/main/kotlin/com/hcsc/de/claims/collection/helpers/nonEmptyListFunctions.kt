package com.hcsc.de.claims.collection.helpers

import com.hcsc.de.claims.results.Failure
import com.hcsc.de.claims.results.Result
import com.hcsc.de.claims.results.asSuccess

fun <type> nonEmptyListOf(vararg elements: type): Result<String, NonEmptyList<type>> = elements.asList().asNonEmptyList()

fun <type> nonEmptyListOf(element: type): NonEmptyList<type> = SimpleNonEmptyList(first = element)

fun <type> nonEmptyListOf(one: type, two: type): NonEmptyList<type> = SimpleNonEmptyList(first = one, remaining = listOf(two))

fun <type> nonEmptyListOf(one: type, two: type, three: type): NonEmptyList<type> =
        SimpleNonEmptyList(first = one, remaining = listOf(two, three))

fun <type> nonEmptyListOf(one: type, two: type, three: type, four: type): NonEmptyList<type> =
        SimpleNonEmptyList(first = one, remaining = listOf(two, three, four))

fun <type> nonEmptyListOf(one: type, two: type, three: type, four: type, five: type): NonEmptyList<type> =
        SimpleNonEmptyList(first = one, remaining = listOf(two, three, four, five))

fun <type> nonEmptyListOf(one: type, two: type, three: type, four: type, five: type, six: type): NonEmptyList<type> =
        SimpleNonEmptyList(first = one, remaining = listOf(two, three, four, five, six))

fun <type> nonEmptyListOf(one: type, two: type, three: type, four: type, five: type, six: type, seven: type
): NonEmptyList<type> = SimpleNonEmptyList(first = one, remaining = listOf(two, three, four, five, six, seven))

// TODO should return a failure object that is something like EmptyListFailure instead of a String
fun <type> List<type>.asNonEmptyList(): Result<String, NonEmptyList<type>> =
        firstOrNull()?.let { first ->

            SimpleNonEmptyList(first = first, remaining = drop(1)).asSuccess<String, NonEmptyList<type>>()

        } ?: Failure("List must contain at least one value")

fun <type> List<type>.plus(elements: NonEmptyList<type>): NonEmptyList<type> =
        firstOrNull()?.let { first ->

            SimpleNonEmptyList(first = first, remaining = drop(1).plus(elements.all))

        } ?: elements