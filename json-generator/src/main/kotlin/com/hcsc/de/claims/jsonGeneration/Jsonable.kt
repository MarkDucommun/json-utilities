package com.hcsc.de.claims.jsonGeneration

sealed class Jsonable<valueType> {

    abstract val ejected: Any
}

class TextJson(private val value: String) : Jsonable<String>() {
    override val ejected: String = value
}

class ListJson(private val value: List<Jsonable<*>>) : Jsonable<List<Jsonable<*>>>() {
    override val ejected: List<Any> = value.map { it.ejected }
}

class ObjectJson(private val value: Map<String, Jsonable<*>>) : Jsonable<Map<String, Jsonable<*>>>() {
    override val ejected: Map<String, Any> = value.mapValues { it ->  it.value.ejected }
}