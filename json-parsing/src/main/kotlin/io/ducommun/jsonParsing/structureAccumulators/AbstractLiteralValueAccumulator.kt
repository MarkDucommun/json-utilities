package io.ducommun.jsonParsing.structureAccumulators

import com.hcsc.de.claims.results.Failure
import com.hcsc.de.claims.results.Result
import com.hcsc.de.claims.results.flatMap
import io.ducommun.jsonParsing.*

abstract class AbstractLiteralValueAccumulator<out outerPreviousClosableType : MainStructure<*>>
    : BaseAccumulator<LiteralValue, LiteralStructureElement, LiteralElement>() {

    abstract val outerPreviousClosable: outerPreviousClosableType

    fun closeLiteralAndEnclosingArray(): Result<String, Accumulator<*, *>> =
            closeLiteralAndEnclosingStructure(::ArrayClose)

    fun closeLiteralAndEnclosingObject(): Result<String, Accumulator<*, *>> =
        closeLiteralAndEnclosingStructure(::ObjectClose)

    inline fun <reified elementType: Close> closeLiteralAndEnclosingStructure(
            crossinline elementConstructor: (Long) -> elementType
    ): Result<String, Accumulator<*, *>> =
            closeLiteral().flatMap { (it as BaseAccumulator<*, *, *>).closeStructure(elementConstructor) }

    fun closeLiteral(): Result<String, Accumulator<*, *>> =
            replaceLastElementAndCloseStructure(LiteralClose(
                    id = previousElement.id,
                    value = previousElement.value
            ))
}