package com.wearefrancis.authenticator

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class Authenticator

fun main(args: Array<String>) {
    SpringApplication.run(Authenticator::class.java, *args)
}