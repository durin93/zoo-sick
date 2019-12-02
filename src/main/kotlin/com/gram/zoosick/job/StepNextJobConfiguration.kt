package com.gram.zoosick.job

import mu.KLogging
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.lang.IllegalArgumentException


@Configuration
class StepNextJobConfiguration(
        val jobBuilderFactory: JobBuilderFactory,
        val stepBuilderFactory: StepBuilderFactory
) {

    companion object: KLogging()

    @Bean
    fun stepNextJob(): Job {
        return jobBuilderFactory.get("stepNextJob")
                .start(step1())
                .next(step2())
                .next(step3())
                .build()
    }

    @Bean
    fun step1(): Step {
        return stepBuilderFactory.get("step1")
                .tasklet { contribution, chunkContext ->
                    logger.info(">>>>> This is step1")
                    RepeatStatus.FINISHED
                }
                .build()
    }

    @Bean
    fun step2(): Step {
        return stepBuilderFactory.get("step2")
                .tasklet { contribution, chunkContext ->
                    logger.info(">>>>> This is step2")
                    RepeatStatus.FINISHED
                }
                .build()
    }

    @Bean
    fun step3(): Step {
        return stepBuilderFactory.get("step3")
                .tasklet { contribution, chunkContext ->
                    logger.info(">>>>> This is step3")
                    RepeatStatus.FINISHED
                }
                .build()
    }

}