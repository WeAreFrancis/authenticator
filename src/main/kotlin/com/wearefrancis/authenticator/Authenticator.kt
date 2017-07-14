package com.wearefrancis.authenticator

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import java.time.Clock

@SpringBootApplication
open class Authenticator {
    @Bean
    open fun clock() = Clock.systemDefaultZone()!!
}

fun main(args: Array<String>) {
    SpringApplication.run(Authenticator::class.java, *args)
}