package io.ducommun.jsonParsing

import com.hcsc.de.claims.results.*

class JsonStructureNester {

    fun nest(structure: List<JsonStructure>): Result<String, MainStructure<*>> {

        return (structure.firstOrNull() matches structure.lastOrNull()).flatMap { (open, close) ->

            val nestedStructure = open.structureConstructor.invoke(open.id, emptyList())

            when (nestedStructure) {
                is LiteralStructureElement -> nestedStructure.addChildren(
                        id = open.id,
                        children = structure
                )
                is StringStructureElement -> nestedStructure.addChildren(
                        id = open.id,
                        children = structure.withoutEnclosingStructures
                )
                is ArrayStructureElement -> nestedStructure.addChildren(
                        id = open.id,
                        children = structure.withoutEnclosingStructures
                )
                is OpenObjectStructure -> nestedStructure.addChildren(
                        id = open.id,
                        children = structure.withoutEnclosingStructures
                )
                is EmptyStructureElement -> TODO()
                is ObjectWithKeyStructure -> TODO()
            }
        }
    }

    fun LiteralStructureElement.addChildren(
            id: Long,
            children: List<JsonStructure>
    ): Result<String, MainStructure<*>> {

        return children
                .ensureIdsMatch(id)
                .flatMap { it.castChildren<LiteralElement>() }
                .map { copy(children = it) }
    }

    fun StringStructureElement.addChildren(
            id: Long,
            children: List<JsonStructure>
    ): Result<String, MainStructure<*>> {

        return children
                .ensureIdsMatch(id)
                .flatMap { it.castChildren<StringValue>() }
                .map { copy(children = it) }
    }

    fun ArrayStructureElement.addChildren(
            id: Long,
            children: List<JsonStructure>
    ): Result<String, MainStructure<*>> {

        return if (children.isEmpty()) {
            Success<String, MainStructure<*>>(this)
        } else {
            children
                    .splitBy<ArrayComma>(id)
                    .map { it.asArrayChild }
                    .traverse()
                    .map { copy(children = it) }
        }
    }

    fun OpenObjectStructure.addChildren(
            id: Long,
            children: List<JsonStructure>
    ): Result<String, MainStructure<*>> {

        return if (children.isEmpty()) {
            Success<String, MainStructure<*>>(this)
        } else {
            children
                    .splitBy<ObjectComma>(id)
                    .map { it.asObjectChild(id) }
                    .traverse()
                    .map { copy(children = it) }
        }
    }

    private val List<JsonStructure>.isNotEmpty: Result<Unit, List<JsonStructure>>
        get() = if (isNotEmpty()) Success(this) else Failure(Unit)

    private val List<JsonStructure>.asArrayChild: Result<String, MainStructure<*>> get() {
        return isNotEmpty
                .flatMapError { Failure<String, List<JsonStructure>>("Invalid JSON - array child can't be empty") }
                .flatMap { nest(it) }
    }

    private fun List<JsonStructure>.asObjectChild(id: Long): Result<String, ObjectChildElement<*>> {

        return isNotEmpty
                .flatMapError { Failure<String, List<JsonStructure>>("") }
                .flatMap {
                    it.splitBy<ObjectColon>(id)
                            .asTwoMembers
                            .flatMap { (key, value) -> (nest(key) to nest(value)).traverse() }
                            .flatMap { it.asStringKeyAndValue }
                            .map { (key, value) -> ObjectChildElement(id = value.id, key = key, value = value) }
                }
    }

    private val Pair<MainStructure<*>, MainStructure<*>>.asStringKeyAndValue:
            Result<String, Pair<StringStructureElement, MainStructure<*>>> get() {

        val (key, value) = this

        return if (key is StringStructureElement) {
            Success<String, Pair<StringStructureElement, MainStructure<*>>>(Pair(key, value))
        } else {
            TODO()
        }
    }

    private val List<List<JsonStructure>>.asTwoMembers:
            Result<String, Pair<List<JsonStructure>, List<JsonStructure>>>
        get() = if (this.size == 2) Success(Pair(first(), last())) else TODO()

    inline fun <reified childType : JsonStructure> List<JsonStructure>.castChildren(): Result<String, List<childType>> {

        val initial: Result<String, List<childType>> = Success(emptyList<childType>())

        return fold(initial) { result, child ->

            result.flatMap { castChildList ->

                when (child) {
                    is childType -> Success<String, List<childType>>(castChildList.plus(child))
                    else -> Failure<String, List<childType>>("Invalid JSON - something went wrong")
                }
            }
        }
    }

    fun List<JsonStructure>.ensureIdsMatch(id: Long? = null): Result<String, List<JsonStructure>> {

        val ids = map(JsonStructure::id).toSet()

        return if (size == 0 || (ids.size == 1 && (id == null || id == ids.first()))) {
            Success(this)
        } else {
            Failure("Invalid JSON - something went wrong")
        }
    }

    inline fun <reified elementType : JsonStructure> List<JsonStructure>.splitBy(id: Long): List<List<JsonStructure>> =

            fold(StructureAccumulator()) { accumulator, structure ->

                if (structure is elementType && structure.id == id) {
                    accumulator.closeCurrentList
                } else {
                    accumulator.addStructure(structure)
                }
            }.let { it.finalStructure }

    val List<JsonStructure>.withoutEnclosingStructures: List<JsonStructure> get() = drop(1).dropLast(1)

    infix fun JsonStructure?.matches(close: JsonStructure?): Result<String, OpenClose> =
            when (this) {
                is StringElement -> this.typedMatch<StringElement>(close)
                is LiteralElement -> this.typedMatch<LiteralElement>(close)
                is ArrayElement -> this.typedMatch<ArrayElement>(close)
                is ObjectElement -> this.typedMatch<ObjectElement>(close)
                else -> Failure("Invalid JSON - no elements passed in")
            }

    inline fun <reified openType : JsonStructure> openType?.typedMatch(close: JsonStructure?): Result<String, OpenClose> =
            if (this is Open<*, *> && close is Close && close is openType && id == close.id) {
                Success(OpenClose(open = this, close = close))
            } else {
                Failure("Invalid JSON - start and end of structure don't match")
            }

    data class OpenClose(
            val open: Open<*, *>,
            val close: Close
    )

    data class StructureAccumulator(
            val lists: List<List<JsonStructure>> = emptyList(),
            val currentList: List<JsonStructure> = emptyList()
    ) {

        fun addStructure(value: JsonStructure): StructureAccumulator =
                copy(currentList = currentList.plus(value))

        val closeCurrentList: StructureAccumulator
            get() = copy(lists = lists.plus(element = currentList), currentList = emptyList())

        val finalStructure: List<List<JsonStructure>> get() = lists.plus(element = currentList)
    }
}