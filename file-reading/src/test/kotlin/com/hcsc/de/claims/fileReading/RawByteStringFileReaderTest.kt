package com.hcsc.de.claims.fileReading

import com.hcsc.de.claims.results.failsAnd
import com.hcsc.de.claims.results.succeedsAnd
import org.assertj.core.api.KotlinAssertions.assertThat
import org.junit.Test
import java.io.File

class RawByteStringFileReaderTest {

    val stringFileReader = RawByteStringFileReader()

    @Test
    fun `it reads the test file from a path into a string`() {

        stringFileReader.read("src/test/resources/test.txt") succeedsAnd { string ->

            assertThat(string).isEqualTo("Hello, world!")
        }
    }

    @Test
    fun `it reads the test file into a string`() {

        val file = File("src/test/resources/test.txt")

        stringFileReader.read(file) succeedsAnd { string ->

            assertThat(string).isEqualTo("Hello, world!")
        }
    }

    @Test
    fun `if it cannot find the file, it returns a Failure`() {

        stringFileReader.read("file-that-does-not-exist") failsAnd { message ->

            assertThat(message).isEqualTo("File not found")
        }
    }
}