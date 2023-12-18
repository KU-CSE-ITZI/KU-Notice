package com.kunotice.kunotice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
class KuNoticeApplication

fun main(args: Array<String>) {
    runApplication<KuNoticeApplication>(*args)
}
