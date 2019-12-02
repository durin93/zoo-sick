package com.gram.zoosick.job

import mu.KLogging
import org.springframework.batch.core.ExitStatus
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
class StepNextConditionalJobConfiguration(
        val jobBuilderFactory: JobBuilderFactory,
        val stepBuilderFactory: StepBuilderFactory
) {

    companion object: KLogging()

    @Bean
    fun stepNextConditionalJob(): Job {
        return jobBuilderFactory.get("stepNextConditionalJob")
                .start(conditionalJobStep1())
                    .on("FAILED")//실패일경우
                    .to(conditionalJobStep3())//3으로
                    .on("*")//3의결과와 상관없이
                    .end() //3의 결과와 상관없이 플로우 종료
                .from(conditionalJobStep1()) //step1 로부터
                    .on("*") //FAILED 외의 모든경우
                    .to(conditionalJobStep2()) //2로 이동
                    .next(conditionalJobStep3()) //step2 정상종료시 step3
                    .on("*") //3의 결과와 상관없이
                    .end()
                .end()
                .build()
    }

    @Bean
    fun conditionalJobStep1(): Step {
        return stepBuilderFactory.get("step1")
                .tasklet { contribution, chunkContext ->
                    logger.info(">>>>> This is stepNextConditionalJob Step1");
                    /**
                    ExitStatus를 FAILED로 지정한다.
                    해당 status를 보고 flow가 진행된다.
                     **/
//                    contribution.exitStatus = ExitStatus.FAILED
                    RepeatStatus.FINISHED;
                }
                .build()
    }

    @Bean
    fun conditionalJobStep2(): Step {
        return stepBuilderFactory.get("step2")
                .tasklet { contribution, chunkContext ->
                    logger.info(">>>>> This is stepNextConditionalJob Step2");
                    RepeatStatus.FINISHED;
                }
                .build()
    }

    @Bean
    fun conditionalJobStep3(): Step {
        return stepBuilderFactory.get("step3")
                .tasklet { contribution, chunkContext ->
                    logger.info(">>>>> This is stepNextConditionalJob Step3");
                    RepeatStatus.FINISHED;
                }
                .build()
    }

}