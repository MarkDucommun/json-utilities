package com.hcsc.de.claims

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import kotlin.Array

@SpringBootApplication
class FakeClaimsGeneratorApplication

fun main(args: Array<String>) {
    SpringApplication.run(FakeClaimsGeneratorApplication::class.java, *args)
}
