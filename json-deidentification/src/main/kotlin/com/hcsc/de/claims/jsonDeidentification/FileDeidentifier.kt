package com.hcsc.de.claims.jsonDeIdentifier

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.javafaker.Faker
import java.io.File
import java.io.FileWriter

// TODO remove from this git repo
class FileDeidentifier {

    val jsonDeidentifer = JsonDeIdentifier(ObjectMapper())

    fun deidentify() {

        val fiveTen = "/Users/xpdesktop/workspace/json-schema-parser/src/main/resources/yan0510.json"
        val fiveEleven = "/Users/xpdesktop/workspace/json-schema-parser/src/main/resources/yan0511.json"

        val allClaims = String(File(fiveTen).readBytes()).split("\n").plus(String(File(fiveEleven).readBytes()).split("\n"))

        val writer1 = FileWriter(File("deidentifiedClaims1.json"))
        val writer2 = FileWriter(File("deidentifiedClaims2.json"))
        val writer3 = FileWriter(File("deidentifiedClaims3.json"))
        val writer4 = FileWriter(File("deidentifiedClaims4.json"))

        val faker = Faker()


        try {
            allClaims.mapIndexed { i: Int, it: String ->
                when (i % 4) {
                    0 -> writer1
                    1 -> writer2
                    2 -> writer3
                    3 -> writer4
                    else -> TODO()
                }.write(faker.numerify(faker.letterify(jsonDeidentifer.deidentifyJson(it))) + "\n")
            }
        } catch (e: Exception) {
        } finally {
            writer1.close()
            writer2.close()
            writer3.close()
            writer4.close()
        }
    }
}