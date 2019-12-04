package com.gram.zoosick.controller

import com.gram.zoosick.domain.stock.StockInfoReturn
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class StockApiControllerTest {
    @Autowired
    lateinit var client: WebTestClient

    @Test
    fun getAllStockInfoByCondition() {
        client.get()
                .uri { builder -> builder.path("/api/stock/list")
                        .queryParam("name","dram")
                        .queryParam("per","1.5")
                        .build()}
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBodyList(StockInfoReturn::class.java)
    }
}