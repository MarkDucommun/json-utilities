package com.hcsc.de.claims.jsonParsingFour.accumulators

import com.hcsc.de.claims.helpers.Failure
import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.Success
import com.hcsc.de.claims.helpers.flatMap
import com.hcsc.de.claims.jsonParsingFour.*
import kotlin.reflect.KClass

abstract class BaseAccumulator<out previousElementType : JsonStructure, out previousClosableType : MainStructure>
    : Accumulator<previousElementType, previousClosableType> {

    fun openString(): Result<String, Accumulator<*, *>> = openStructure(::StringOpen)

    fun openArray(): Result<String, Accumulator<*, *>> = openStructure(::ArrayOpen)

    fun openObject(): Result<String, Accumulator<*, *>> = openStructure(::ObjectOpen)

    fun openLiteral(char: Char): Result<String, Accumulator<*, *>> =
            openStructure(LiteralValue(id = idCounter, value = char))

    inline fun <reified elementType : Open<*>> openStructure(
            constructor: (Long) -> elementType
    ): Result<String, Accumulator<*, *>> = openStructure(constructor(idCounter))

    inline fun <reified elementType : WithValue> addValue(
            constructor: (Long, Char) -> elementType,
            char: Char
    ): Result<String, Accumulator<*, *>> = addElement(constructor(previousClosable.id, char))

    inline fun <reified elementType : JsonStructure> addElement(
            constructor: (Long) -> elementType
    ): Result<String, Accumulator<*, *>> = addElement(constructor(previousClosable.id))

    inline fun <reified elementType : Close> closeStructure(
            constructor: (Long) -> elementType
    ): Result<String, Accumulator<*, *>> = closeStructure(constructor(previousClosable.id))

    inline fun <reified elementType : Open<structureType>, reified structureType : MainStructure> openStructure(
            element: elementType
    ): Result<String, Accumulator<*, *>> {

        val structureElement: structureType = element.structureConstructor(idCounter)

        val structureClass: KClass<out structureType> = structureElement::class
        val elementClass: KClass<elementType> = elementType::class

        return findCastConstructor(elementClass, structureClass).flatMap { constructorHolder ->
            Success<String, Accumulator<*, *>>(constructorHolder.accumulatorConstructor(
                    idCounter + 1,
                    structure.plus(element),
                    structureStack.plus(structureElement),
                    element,
                    structureElement
            ))
        }
    }

    inline fun <reified elementType : JsonStructure> setPreviousElement(
            element: elementType
    ): Result<String, Accumulator<*, *>> {

        val structureClass: KClass<out previousClosableType> = previousClosable::class
        val elementClass: KClass<elementType> = elementType::class

        return findCastConstructor(elementClass, structureClass).flatMap { constructorHolder ->
            Success<String, Accumulator<*, *>>(constructorHolder.accumulatorConstructor(
                    idCounter,
                    structure,
                    structureStack,
                    element,
                    previousClosable
            ))
        }
    }

    inline fun <reified elementType : JsonStructure> addElement(element: elementType): Result<String, Accumulator<*, *>> {

        val structureClass: KClass<out previousClosableType> = previousClosable::class
        val elementClass: KClass<elementType> = elementType::class

        return findCastConstructor(elementClass, structureClass).flatMap { constructorHolder ->
            Success<String, Accumulator<*, *>>(constructorHolder.accumulatorConstructor(
                    idCounter,
                    structure.plus(element),
                    structureStack,
                    element,
                    previousClosable
            ))
        }
    }

    inline fun <reified elementType : Close> closeStructure(element: elementType): Result<String, Accumulator<*, *>> {

        val newStructureStack = structureStack.dropLast(1)
        val newPreviousClosable = newStructureStack.last()

        val transformedPreviousClosable = when (newPreviousClosable) {
            is OpenObjectStructure -> ObjectWithKeyStructure(id = newPreviousClosable.id)
            is ObjectWithKeyStructure -> OpenObjectStructure(id = newPreviousClosable.id)
            else -> newPreviousClosable
        }

        val structureClass: KClass<out MainStructure> = transformedPreviousClosable::class
        val elementClass: KClass<elementType> = elementType::class

        return findCastConstructor(elementClass, structureClass).flatMap { constructorHolder ->
            Success<String, Accumulator<*, *>>(constructorHolder.accumulatorConstructor(
                    idCounter,
                    structure.plus(element),
                    newStructureStack.dropLast(1).plus(transformedPreviousClosable),
                    element,
                    transformedPreviousClosable
            ))
        }
    }

    inline fun <reified elementType : Close> replaceLastElementAndCloseStructure(
            element: elementType
    ): Result<String, Accumulator<*, *>> {

        val newStructureStack = structureStack.dropLast(1)
        val newPreviousClosable = newStructureStack.last()

        val transformedPreviousClosable = when (newPreviousClosable) {
            is OpenObjectStructure -> ObjectWithKeyStructure(id = newPreviousClosable.id)
            is ObjectWithKeyStructure -> OpenObjectStructure(id = newPreviousClosable.id)
            else -> newPreviousClosable
        }

        val structureClass: KClass<out MainStructure> = transformedPreviousClosable::class
        val elementClass: KClass<elementType> = elementType::class

        return findCastConstructor(elementClass, structureClass).flatMap { constructorHolder ->
            Success<String, Accumulator<*, *>>(constructorHolder.accumulatorConstructor(
                    idCounter,
                    structure.dropLast(1).plus(element),
                    newStructureStack.dropLast(1).plus(transformedPreviousClosable),
                    element,
                    transformedPreviousClosable
            ))
        }
    }

    inline fun <reified lastElementType : Close, reified newElementType: JsonStructure> replaceLastElementAndAddNewElementAndCloseStructure(
            lastElement: lastElementType,
            newElement: newElementType
    ): Result<String, Accumulator<*, *>> {

        val newStructureStack = structureStack.dropLast(1)
        val newPreviousClosable = newStructureStack.last()

        val transformedPreviousClosable = when (newPreviousClosable) {
            is OpenObjectStructure -> ObjectWithKeyStructure(id = newPreviousClosable.id)
            is ObjectWithKeyStructure -> OpenObjectStructure(id = newPreviousClosable.id)
            else -> newPreviousClosable
        }

        val structureClass: KClass<out MainStructure> = transformedPreviousClosable::class
        val elementClass: KClass<newElementType> = newElementType::class

        return findCastConstructor(elementClass, structureClass).flatMap { constructorHolder ->
            Success<String, Accumulator<*, *>>(constructorHolder.accumulatorConstructor(
                    idCounter,
                    structure.dropLast(1).plus(lastElement).plus(newElement),
                    newStructureStack.dropLast(1).plus(transformedPreviousClosable),
                    newElement,
                    transformedPreviousClosable
            ))
        }
    }

    fun fail(message: String): Failure<String, Accumulator<*, *>> = Failure("Invalid JSON - $message")

    val unmodified: Success<String, Accumulator<*, *>> get() = Success(this)

    val enclosingStructure: MainStructure = try {
        structureStack.dropLast(1).last()
    } catch (e: Exception) {
        EmptyStructureElement
    }

    fun <elementType : JsonStructure, structureType : MainStructure> findCastConstructor(
            elementClass: KClass<elementType>,
            structureClass: KClass<out structureType>
    ): Result<String, ConstructorHolder<elementType, structureType>> =
            findConstructor(elementClass = elementClass, structureClass = structureClass)
                    .flatMap { constructorHolder ->
                        try {
                            Success<String, ConstructorHolder<elementType, structureType>>(
                                    constructorHolder as ConstructorHolder<elementType, structureType>
                            )
                        } catch (e: Exception) {
                            Failure<String, ConstructorHolder<elementType, structureType>>("Could not cast constructor")
                        }
                    }

    fun <elementType : JsonStructure, structureType : MainStructure> findConstructor(
            elementClass: KClass<elementType>,
            structureClass: KClass<out structureType>
    ): Result<String, ConstructorHolder<*, *>> =
            constructors
                    .find { it.elementClass == elementClass && it.structureClass == structureClass }
                    ?.let { Success<String, ConstructorHolder<*, *>>(it) }
                    ?: Failure("Could not find constructor: ${elementClass.simpleName}, ${structureClass.simpleName}")


    data class ConstructorHolder<elementType : JsonStructure, structureType : MainStructure>(
            val elementClass: KClass<elementType>,
            val structureClass: KClass<structureType>,
            val accumulatorConstructor: (Long, List<JsonStructure>, List<MainStructure>, elementType, structureType) -> Accumulator<elementType, structureType>
    )

    companion object {

        val constructors: List<ConstructorHolder<*, *>> = listOf(
                // String ----------------------------------------------------------------------------------------------
                ConstructorHolder(
                        elementClass = StringOpen::class,
                        structureClass = StringStructureElement::class,
                        accumulatorConstructor = ::StringOpenAccumulator
                ),
                ConstructorHolder(
                        elementClass = StringValue::class,
                        structureClass = StringStructureElement::class,
                        accumulatorConstructor = ::StringValueAccumulator
                ),
                ConstructorHolder(
                        elementClass = StringEscape::class,
                        structureClass = StringStructureElement::class,
                        accumulatorConstructor = ::StringEscapeAccumulator
                ),
                ConstructorHolder(
                        elementClass = StringClose::class,
                        structureClass = EmptyStructureElement::class,
                        accumulatorConstructor = ::StringCloseEmptyAccumulator
                ),
                ConstructorHolder(
                        elementClass = StringClose::class,
                        structureClass = ArrayStructureElement::class,
                        accumulatorConstructor = ::StringCloseArrayAccumulator
                ),
                ConstructorHolder(
                        elementClass = StringClose::class,
                        structureClass = OpenObjectStructure::class,
                        accumulatorConstructor = ::StringCloseOpenObjectAccumulator
                ),
                // Literal ---------------------------------------------------------------------------------------------
                ConstructorHolder(
                        elementClass = LiteralValue::class,
                        structureClass = LiteralStructureElement::class,
                        accumulatorConstructor = ::LiteralValueAccumulator
                ),
                ConstructorHolder(
                        elementClass = LiteralClose::class,
                        structureClass = EmptyStructureElement::class,
                        accumulatorConstructor = ::LiteralCloseEmptyAccumulator
                ),
                ConstructorHolder(
                        elementClass = LiteralClose::class,
                        structureClass = ArrayStructureElement::class,
                        accumulatorConstructor = ::LiteralCloseArrayAccumulator
                ),
                ConstructorHolder(
                        elementClass = LiteralClose::class,
                        structureClass = OpenObjectStructure::class,
                        accumulatorConstructor = ::LiteralCloseOpenObjectAccumulator
                ),
                // Array  ----------------------------------------------------------------------------------------------
                ConstructorHolder(
                        elementClass = ArrayOpen::class,
                        structureClass = ArrayStructureElement::class,
                        accumulatorConstructor = ::ArrayOpenAccumulator
                ),
                ConstructorHolder(
                        elementClass = ArrayComma::class,
                        structureClass = ArrayStructureElement::class,
                        accumulatorConstructor = ::ArrayCommaAccumulator
                ),
                ConstructorHolder(
                        elementClass = ArrayClose::class,
                        structureClass = EmptyStructureElement::class,
                        accumulatorConstructor = ::ArrayCloseEmptyAccumulator
                ),
                ConstructorHolder(
                        elementClass = ArrayClose::class,
                        structureClass = ArrayStructureElement::class,
                        accumulatorConstructor = ::ArrayCloseArrayAccumulator
                ),
                ConstructorHolder(
                        elementClass = ArrayClose::class,
                        structureClass = OpenObjectStructure::class,
                        accumulatorConstructor = ::ArrayCloseOpenObjectAccumulator
                ),
                // Object ----------------------------------------------------------------------------------------------
                ConstructorHolder(
                        elementClass = ObjectOpen::class,
                        structureClass = OpenObjectStructure::class,
                        accumulatorConstructor = ::ObjectOpenAccumulator
                ),
                ConstructorHolder(
                        elementClass = StringClose::class,
                        structureClass = ObjectWithKeyStructure::class,
                        accumulatorConstructor = ::ObjectWithKeyAccumulator
                ),
                ConstructorHolder(
                        elementClass = ObjectColon::class,
                        structureClass = ObjectWithKeyStructure::class,
                        accumulatorConstructor = ::ObjectReadyForValueAccumulator
                ),
                ConstructorHolder(
                        elementClass = ObjectComma::class,
                        structureClass = OpenObjectStructure::class,
                        accumulatorConstructor = ::ObjectCommaAccumulator
                ),
                ConstructorHolder(
                        elementClass = ObjectClose::class,
                        structureClass = EmptyStructureElement::class,
                        accumulatorConstructor = ::ObjectCloseEmptyAccumulator
                ),
                ConstructorHolder(
                        elementClass = ObjectClose::class,
                        structureClass = ArrayStructureElement::class,
                        accumulatorConstructor = ::ObjectCloseArrayAccumulator
                ),
                ConstructorHolder(
                        elementClass = ObjectClose::class,
                        structureClass = OpenObjectStructure::class,
                        accumulatorConstructor = ::ObjectCloseOpenObjectAccumulator
                )
        )
    }
}