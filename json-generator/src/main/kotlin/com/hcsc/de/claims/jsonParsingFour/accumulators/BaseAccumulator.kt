package com.hcsc.de.claims.jsonParsingFour.accumulators

import com.hcsc.de.claims.helpers.Failure
import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.Success
import com.hcsc.de.claims.jsonParsingFour.*

abstract class BaseAccumulator<out previousElementType : JsonStructure, out previousClosableType : MainStructure?>
    : Accumulator<previousElementType, previousClosableType> {

    fun openString(): Result<String, Accumulator<*, *>> =
            openStructure(::StringOpen, ::StringStructureElement, ::StringOpenAccumulator)

    fun openArray(): Result<String, Accumulator<*, *>> =
            openStructure(::ArrayOpen, ::ArrayStructureElement, ::ArrayOpenAccumulator)

    fun openObject(): Result<String, Accumulator<*, *>> =
            openStructure(::ObjectOpen, ::OpenObjectStructure, ::ObjectOpenAccumulator)

    fun openLiteral(char: Char): Result<String, Accumulator<*, *>> =
            openStructure(LiteralValue(id = idCounter, value = char), ::LiteralStructureElement, ::LiteralValueAccumulator)

    fun <T : JsonStructure, U : MainStructure> openStructure(
            elementConstructor: (Long) -> T,
            structureConstructor: (Long) -> U,
            accumulatorConstructor: (Long, List<JsonStructure>, List<MainStructure>, T, U) -> Accumulator<T, U>
    ): Result<String, Accumulator<*, *>> =
            openStructure(
                    element = elementConstructor(idCounter),
                    structureConstructor = structureConstructor,
                    accumulatorConstructor = accumulatorConstructor
            )

    fun <T : JsonStructure, U : MainStructure> openStructure(
            element: T,
            structureConstructor: (Long) -> U,
            accumulatorConstructor: (Long, List<JsonStructure>, List<MainStructure>, T, U) -> Accumulator<T, U>
    ): Result<String, Accumulator<*, *>> {

        return structureConstructor(idCounter).let { newStructure ->

            Success(accumulatorConstructor(
                    idCounter + 1,
                    structure.plus(element),
                    structureStack.plus(newStructure),
                    element,
                    newStructure
            ))
        }

    }

    fun fail(message: String): Failure<String, Accumulator<*, *>> = Failure("Invalid JSON - $message")

    val unmodified: Success<String, Accumulator<*, *>> get() = Success(this)
}