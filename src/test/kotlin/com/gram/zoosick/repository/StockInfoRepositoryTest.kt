package com.gram.zoosick.repository

import com.gram.zoosick.domain.stock.repository.StockInfoRepository
import com.gram.zoosick.domain.stock.repository.StockInfoRepositorySupport
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
class StockInfoRepositoryTest {

    @Autowired
    lateinit var stockInfoRepositorySupport: StockInfoRepositorySupport

    @Test
    fun findByNameWithQuerydslTest() {
        val stockInfo = stockInfoRepositorySupport.findByName("gram")
        println("stockInfo $stockInfo")
    }

    @Test
    fun findAllByNameWithQuerydslTest() {
        val stockInfos = stockInfoRepositorySupport.findAllByCondition("dram","2.00")
        println("stockInfos $stockInfos")
    }

}