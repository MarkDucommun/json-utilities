package com.hcsc.de.claims

import com.hcsc.de.claims.helpers.Result
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock

inline fun <reified T : Translator<inputType, outputType>, reified inputType : Any, outputType>
        mockTranslator(result: Result<String, outputType>): T = mock { on { translate(any()) } doReturn result }