package com.gram.zoosick

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.core.task.TaskExecutor



@SpringBootApplication
@EnableAsync
class ZoosickApplication

fun main(args: Array<String>) {
    runApplication<ZoosickApplication>(*args)
}

@Bean
fun taskExecutor(): TaskExecutor {

    val taskExecutor = ThreadPoolTaskExecutor()
    taskExecutor.corePoolSize = 10
    taskExecutor.setQueueCapacity(50)
    taskExecutor.maxPoolSize = 20

    return taskExecutor
}
