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


@Configuration
class JobConfiguration(
        val jobBuilderFactory: JobBuilderFactory,
        val stepBuilderFactory: StepBuilderFactory
) {

    companion object: KLogging()

    @Bean
    fun simpleJob(): Job {
        return jobBuilderFactory.get("simpleJob")
                .start(simpleStep1(null))
                .next(simpleStep2(null))
                .build()
    }

    @Bean
    @JobScope
    fun simpleStep1(@Value("#{jobParameters[requestDate]}") requestDate: String?): Step {
        return stepBuilderFactory.get("simpleStep1")
                .tasklet { contribution, chunkContext ->
                    logger.info(">>>>> This is Step1")
                    logger.info(">>>>> requestDate = $requestDate")
                    RepeatStatus.FINISHED
                }
                .build()
    }

    @Bean
    @JobScope
    fun simpleStep2(@Value("#{jobParameters[requestDate]}") requestDate: String?): Step {
        return stepBuilderFactory.get("simpleStep1")
                .tasklet { contribution, chunkContext ->
                    logger.info(">>>>> This is Step2")
                    logger.info(">>>>> requestDate = $requestDate")
                    RepeatStatus.FINISHED
                }
                .build()
    }

}