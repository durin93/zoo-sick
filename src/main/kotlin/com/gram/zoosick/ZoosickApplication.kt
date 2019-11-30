package com.gram.zoosick

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.core.task.TaskExecutor
import org.springframework.data.jpa.repository.config.EnableJpaAuditing


@SpringBootApplication
//@EnableAsync
@EnableJpaAuditing
@EnableBatchProcessing
class ZoosickApplication

fun main(args: Array<String>) {
    runApplication<ZoosickApplication>(*args)
}
