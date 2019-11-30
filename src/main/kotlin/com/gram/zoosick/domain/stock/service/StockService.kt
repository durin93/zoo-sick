package com.gram.zoosick.domain.stock.service

import com.gram.zoosick.domain.stock.*
import com.gram.zoosick.domain.stock.entity.StockInfo
import com.gram.zoosick.domain.stock.repository.StockInfoRepository
import mu.KLogging
import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.function.Supplier
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional

@Service
@Transactional
class StockService(
        val stockInfoRepository: StockInfoRepository,
        val stockApiManager: StockApiManager
) {
    @PersistenceContext
    lateinit var entityManager: EntityManager


    companion object : KLogging()

    @Value("\${spring.jpa.properties.hibernate.jdbc.batch_size:50}")
    private val batchSize: Int = 0

    val executors: ExecutorService = Executors.newFixedThreadPool(1000)

    fun getAllCorporationsDetail(): List<StockInfoReturn> {
        return getAllCorporationsCodes()?.let {
            getDetailCorpDetails(it)
        } ?: emptyList()
    }

    private fun getAllCorporationsCodes(): List<String>? {
        val response = stockApiManager.getAllCorporations(CorpSearchRequest().toParams())
        val stockCodes: List<String>? = Jsoup.parseBodyFragment(response.toString()).select("td[style*=@]").text().split(" ")
        return stockCodes ?: emptyList()
    }

    fun asyncCreateCorpDetail(code: String): CompletableFuture<StockInfoReturn> {
        return CompletableFuture.supplyAsync(Supplier {
            stockApiManager.getCorporationsDetails(code)
        }, executors).thenApply { response ->
            val parsedResponse = Jsoup.parseBodyFragment(response.toString())
            val perTable = parsedResponse.getElementsByClass("per_table") //투자정보에 위
            val sectionCopAnalysis = parsedResponse.getElementsByClass("section cop_analysis")?.let { it.first() }//기업실적분석
            val sectionTradeCompare = parsedResponse.getElementsByClass("section trade_compare")?.let { it.first() } //동일업종비
            StockInfoReturn(
                    name = sectionTradeCompare?.let { it.getElementsByClass("h_th2 th_cop_comp1").next().select("a").text() },
                    code = code,
                    currentPrice = sectionTradeCompare?.let { it.getElementsByClass("h_th2 th_cop_comp2").next().text() },
                    sales = sectionTradeCompare?.let { it.getElementsByClass("h_th2 th_cop_comp7").next().text() },
                    lowerTheBetterDto = LowerTheBetterDto(
                            per = perTable.select("em#krx_per").text(),
                            pbr = perTable.select("em#_pbr").text(),
                            debtRatio = sectionCopAnalysis?.let { it.getElementsByClass("h_th2 th_cop_anal14").nextAll().not(".null").last().text() }),
                    higherTheBetterDto = HigherTheBetterDto(
                            netIncome = sectionTradeCompare?.let { it.getElementsByClass("h_th2 th_cop_comp10").next().text() },
                            roe = sectionTradeCompare?.let { it.getElementsByClass("h_th2 th_cop_comp12").next().text() },
                            eps = perTable.select("em#krx_eps").text(),
                            bps = perTable.select("em#_pbr").nextAll().select("em").text()
                            /*eps = sectionCopAnalysis.getElementsByClass("h_th2 th_cop_anal17").nextAll().not(".null").last().text(),
                            bps = sectionCopAnalysis.getElementsByClass("h_th2 th_cop_anal20").nextAll().not(".null").last().text()*/
                    )
            )
        }.exceptionally { throwable ->
            logger.info("error $throwable")
            logger.info("error ${throwable.message}")
            logger.info("error ${throwable.printStackTrace()}")
            return@exceptionally null
        }
    }

    private fun getDetailCorpDetails(stockCodes: List<String>): List<StockInfoReturn> {
        return stockCodes.map {
            Thread.sleep(100)
            asyncCreateCorpDetail(it)
        }.allOfJoin()
    }

    fun searchCorpsByCondition(searchCorpCondition: SearchCorpCondition): List<StockInfoReturn>? {
        val stockCodes = getAllCorporationsCodes()
        return stockCodes?.let { getDetailCorpDetails(it) }
    }

    fun saveStockInfoList(stockInfoList: List<StockInfoReturn>) {
        stockInfoList.filterNotNull().map { it.toStockInfo() }.let{ it ->
//        stockInfoList.filterNotNull().distinctBy { it.code }.map { it.toStockInfo() }.let{
//            stockInfoRepository.deleteAllByIdInQuery(it.map { it.id })
            stockInfoRepository.deleteAll()
            stockInfoRepository.saveAll(it)
        }
    }


    //안돼~!@!@!@
    private fun bulkInsert(stockInfoList: List<StockInfo>) {
        logger.info { "bulkInsert start $stockInfoList" }
        for ((idx, stockInfo) in stockInfoList.withIndex()) {
            entityManager.persist(stockInfo)
            if (idx > 0 && idx % batchSize == 0) {
                entityManager.flush()
                entityManager.clear()
            }
        }
        entityManager.flush()
        entityManager.clear()
    }

    fun updateAllStockInfo() {
        getAllCorporationsDetail()?.let { it ->
            saveStockInfoList(it)
        }
    }
}


fun <T> List<CompletableFuture<out T>>.allOf(): CompletableFuture<List<T>> {
    return CompletableFuture
            .allOf(*toTypedArray())
            .thenApply {
                map(CompletableFuture<out T>::join)
            }
}

fun <T> List<CompletableFuture<out T>>.allOfJoin() = allOf().join()!!