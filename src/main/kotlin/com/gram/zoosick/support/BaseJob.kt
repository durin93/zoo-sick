package com.gram.zoosick.support

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.boot.autoconfigure.batch.BatchProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary



@Configuration
@EnableBatchProcessing
abstract class BaseJob {
    @Autowired
    lateinit var jobBuilderFactory: JobBuilderFactory
    @Autowired
    lateinit var stepBuilderFactory: StepBuilderFactory

}