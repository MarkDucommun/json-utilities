package com.hcsc.de.claims.jsonParsingFour

import com.hcsc.de.claims.helpers.*
import sun.applet.Main

class JsonStructureNester {

    fun nest(structure: List<JsonStructure>): Result<String, MainStructure<*>> {

        val first = structure.first()
        val last = structure.last()

        return if (first is Open<*, *> && last is Close && first.id == last.id) {

            val nestedStructure = first.structureConstructor.invoke(first.id, emptyList())

            when (nestedStructure) {
                is EmptyStructureElement -> TODO()
                is LiteralStructureElement -> {

                    structure.ensureIdsMatch().flatMap {

                        val initial: Result<String, List<LiteralElement>> = Success(emptyList<LiteralElement>())

                        structure.fold(initial) { result, child ->

                            result.flatMap { literalValueList ->

                                when (child) {
                                    is LiteralElement -> Success<String, List<LiteralElement>>(literalValueList.plus(child))
                                    else -> Failure<String, List<LiteralElement>>("Invalid JSON - something went wrong")
                                }
                            }
                        }.map { children ->
                            nestedStructure.copy(children = children) as MainStructure<*>
                        }
                    }
                }
                is StringStructureElement -> {

                    structure.drop(1).dropLast(1).ensureIdsMatch(structure.firstOrNull()?.id).flatMap { children ->

                        val initial: Result<String, List<StringValue>> = Success(emptyList<StringValue>())

                        children.fold(initial) { result, child ->

                            result.flatMap { stringValueList ->

                                when (child) {
                                    is StringValue -> Success<String, List<StringValue>>(stringValueList.plus(child))
                                    else -> Failure<String, List<StringValue>>("Invalid JSON - something went wrong")
                                }
                            }
                        }.map { children ->
                            nestedStructure.copy(children = children) as MainStructure<*>
                        }
                    }
                }
                is ArrayStructureElement -> {

                    val childrenStructure = structure.drop(1).dropLast(1)

                    if (childrenStructure.isEmpty()) {
                        Success<String, MainStructure<*>>(nestedStructure)
                    } else {
                        val initial: Result<String, ArrayStructureAccumulator> = Success(ArrayStructureAccumulator())

                        childrenStructure.fold(initial) { result, structure ->
                            result.flatMap { accumulator ->
                                when (structure) {
                                    is ArrayComma -> if (accumulator.currentList.isEmpty()) {
                                        Failure<String, ArrayStructureAccumulator>("Invalid JSON - array child can't be empty")
                                    } else {
                                        Success<String, ArrayStructureAccumulator>(
                                                accumulator.copy(
                                                        lists = accumulator.lists.plus(element = accumulator.currentList),
                                                        currentList = emptyList()
                                                )
                                        )
                                    }
                                    else -> Success<String, ArrayStructureAccumulator>(
                                            accumulator.copy(currentList = accumulator.currentList.plus(structure))
                                    )
                                }
                            }
                        }.flatMap { accumulator ->
                            if (accumulator.currentList.isEmpty()) {
                                Failure<String, MainStructure<*>>("")
                            } else {

                                accumulator.lists.plus(element = accumulator.currentList).map { childStructure ->

                                    nest(childStructure)
                                }.traverse().map { children ->
                                    nestedStructure.copy(children = children) as MainStructure<*>
                                }
                            }
                        }
                    }
                }
                is OpenObjectStructure, is ObjectWithKeyStructure -> TODO()
            }
        } else {
            Failure("Invalid JSON - start and end of structure don't match")
        }
    }

    fun List<JsonStructure>.ensureIdsMatch(id: Long? = null): Result<String, List<JsonStructure>> {

        val ids = map(JsonStructure::id).toSet()

        return if (size == 0 || (ids.size == 1 && id == null || id == ids.first())) {
            Success(this)
        } else {
            Failure("Invalid JSON - something went wrong")
        }
    }

    data class ArrayStructureAccumulator(
            val lists: List<List<JsonStructure>> = emptyList(),
            val currentList: List<JsonStructure> = emptyList()
    )
}