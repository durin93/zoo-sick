package com.gram.zoosick.job

import com.gram.zoosick.support.BaseJob
import mu.KLogging
import org.springframework.batch.core.*
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.lang.IllegalArgumentException
import org.springframework.batch.core.job.flow.JobExecutionDecider
import org.springframework.batch.core.job.flow.FlowExecutionStatus
import java.util.*


@Configuration
class DecideJobConfiguration : BaseJob() {

    companion object: KLogging()


    /*
    분기 로직에 대한 모든 일은 OddDecider가 전담.
    아무리 복잡한 분기로직이 필요하더라도 Step과는 명확히 역할과 책임이 분리된채 진행할 수 있게됨.
     */

    @Bean
    fun deciderJob(): Job {
        return jobBuilderFactory.get("deciderJob")
                .start(startStep())
                .next(decider()) //홀 | 짝 구분
                .from(decider()) //decider 상태가
                    .on("ODD") //odd
                    .to(oddStep())
                .from(decider()) //decider 상태가
                    .on("EVEN") //even
                    .to(evenStep())
                .end()
                .build()
    }

    @Bean
    fun startStep(): Step {
        return stepBuilderFactory.get("startStep")
                .tasklet { contribution, chunkContext ->
                    logger.info(">>>>> Start!");
                    RepeatStatus.FINISHED;
                }
                .build()
    }

    @Bean
    fun evenStep(): Step {
        return stepBuilderFactory.get("evenStep")
                .tasklet { contribution, chunkContext ->
                    logger.info(">>>>> 짝수입니다!");
                    RepeatStatus.FINISHED;
                }
                .build()
    }

    @Bean
    fun oddStep(): Step {
        return stepBuilderFactory.get("oddStep")
                .tasklet { contribution, chunkContext ->
                    logger.info(">>>>> 홀수입니다!");
                    RepeatStatus.FINISHED;
                }
                .build()
    }


    @Bean
    fun decider(): JobExecutionDecider {
        return OddDecider()
    }

    class OddDecider : JobExecutionDecider {

        override fun decide(jobExecution: JobExecution, stepExecution: StepExecution?): FlowExecutionStatus {
            val rand = Random()

            val randomNumber = rand.nextInt(50) + 1
            logger.info{"랜덤숫자: $randomNumber"}

            return if (randomNumber % 2 == 0) {
                FlowExecutionStatus("EVEN")
            } else {
                FlowExecutionStatus("ODD")
            }
        }

    }

}