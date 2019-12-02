package com.gram.zoosick.job

import com.gram.zoosick.domain.stock.entity.StockInfo
import com.gram.zoosick.domain.stock.repository.StockInfoRepository
import com.gram.zoosick.support.BaseJob
import mu.KLogging
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.item.*
import org.springframework.batch.item.database.JpaItemWriter
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*
import javax.persistence.EntityManagerFactory


@Configuration
class StockJobConfiguration(
        val stockInfoRepository: StockInfoRepository,
        val entityManagerFactory: EntityManagerFactory
) : BaseJob() {

    companion object : KLogging()

    val CHUNK_SIZE = 10

    @Bean
    fun inactiveStockJob(jobBuilderFactory: JobBuilderFactory, updateStockInfoStep: Step): Job { //(1)
        return jobBuilderFactory.get("inactiveStockJob")
//                .preventRestart() //(2)
                .start(updateStockInfoStep) //(3)
                .build()
    }

    @Bean
    fun updateStockInfoStep(): Step {
        return stepBuilderFactory.get("updateStockInfoStep")
                .chunk<StockInfo, StockInfo>(CHUNK_SIZE)
                .reader(inactiveStockInfoJpaReader())
//                .reader(inactiveStockInfoReader())
                .processor(inactiveStockProcessor())
                .writer(inactiveStockWriter())
                .build()
    }

    @Bean
    fun inactiveStockInfoReader(): QueueItemReader<StockInfo> {
        val oldStockInfo: List<StockInfo> = stockInfoRepository.findAll()
        return oldStockInfo?.let { it ->
            logger.info { "active reader" }
            QueueItemReader(it)
        } ?: throw NullPointerException("옛날 게 없다.")
    }

    @Bean
    fun inactiveStockProcessor(): ItemProcessor<StockInfo, StockInfo> {
        logger.info { "active processor" }
        return ItemProcessor { stockInfo ->
            stockInfo.updateStatus()
            stockInfo
        }
    }
/*

    @Bean
    fun inactiveStockWriter(): ItemWriter<StockInfo> {
        logger.info { "active writer" }
        return ItemWriter { stocksInfo -> stockInfoRepository.saveAll(stocksInfo) }
    }
*/

    @Bean
    fun inactiveStockWriter(): JpaItemWriter<StockInfo> {
        var jpaItemWriter = JpaItemWriter<StockInfo>()
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter
    }

    @Bean(destroyMethod = "") //(1)
    fun inactiveStockInfoJpaReader(): JpaPagingItemReader<StockInfo> {
        val jpaPagingItemReader = JpaPagingItemReader<StockInfo>()
        jpaPagingItemReader.setQueryString("select s from StockInfo s")

//        val map = HashMap<String, Any>()
//        val now = LocalDateTime.ofInstant(nowDate.toInstant(), ZoneId.systemDefault())
//        map["updatedDate"] = now.minusYears(1)

//        jpaPagingItemReader.setParameterValues(map) //(3)
        jpaPagingItemReader.setEntityManagerFactory(entityManagerFactory) //(4)
        jpaPagingItemReader.pageSize = CHUNK_SIZE //(5)
        return jpaPagingItemReader
    }

    class QueueItemReader<T>(data: List<T>) : ItemReader<T> {
        private val queue: Queue<T>

        init {
            logger.info { "init QueueItemReader $data" }
            this.queue = LinkedList(data) //(1)
        }

        @Throws(Exception::class, UnexpectedInputException::class, ParseException::class, NonTransientResourceException::class)
        override fun read(): T {
            return queue.poll() //(2)
        }
    }

}