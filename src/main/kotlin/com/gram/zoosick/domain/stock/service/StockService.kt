package com.gram.zoosick.domain.stock.service

import com.gram.zoosick.domain.stock.*
import com.gram.zoosick.domain.stock.repository.StockInfoRepository
import com.gram.zoosick.domain.stock.repository.StockInfoRepositorySupport
import mu.KLogging
import org.jsoup.Jsoup
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.function.Supplier
import javax.transaction.Transactional
import org.springframework.cache.annotation.CacheEvict



@Service
@Transactional
class StockService(
        val stockInfoRepository: StockInfoRepository,
        val stockInfoRepositorySupport: StockInfoRepositorySupport,
        val stockApiManager: StockApiManager
) {
    /* for bulkInsert
    @PersistenceContext
    lateinit var entityManager: EntityManager
    @Value("\${spring.jpa.properties.hibernate.jdbc.batch_size:50}")
    private val batchSize: Int = 0
    */

    companion object : KLogging()

    val executors: ExecutorService = Executors.newFixedThreadPool(1000)

    private fun getAllCorporationsDetail(): List<StockInfoReturn> {
        return getAllCorporationsCodes()?.let {
            getDetailCorpDetails(it)
        } ?: emptyList()
    }

    private fun getAllCorporationsCodes(): List<String>? {
        val response = stockApiManager.getAllCorporations(CorpSearchRequest().toParams())
        val stockCodes: List<String>? = Jsoup.parseBodyFragment(response.toString()).select("td[style*=@]").text().split(" ")
        return stockCodes ?: emptyList()
    }

    private fun getDetailCorpDetails(stockCodes: List<String>): List<StockInfoReturn> {
        return stockCodes.map {
            Thread.sleep(100) //for connection refuse prevent
            asyncCreateCorpDetail(it)
        }.allOfJoin()
    }


    private fun saveStockInfoList(stockInfoList: List<StockInfoReturn>) {
        stockInfoList.filterNotNull().map { it.toStockInfo() }.let{ it ->
//        stockInfoList.filterNotNull().distinctBy { it.code }.map { it.toStockInfo() }.let{
//            stockInfoRepository.deleteAllByIdInQuery(it.map { it.id })
            stockInfoRepository.deleteAll()
            stockInfoRepository.saveAll(it)
        }
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

    //안돼~!@!@!@
    /*private fun bulkInsert(stockInfoList: List<StockInfo>) {
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
    }*/

    @CacheEvict(value=["findAllStockInfoByConditionCache"], allEntries = true)
    fun updateAllStockInfo() {
        getAllCorporationsDetail()?.let { it ->
            saveStockInfoList(it)
        }
    }

    @Cacheable(value = ["findAllStockInfoByConditionCache"], key = "#searchCorpCondition.cacheKey()")
    fun findAllStockInfoByConditionCache(searchCorpCondition: SearchCorpCondition): List<StockInfoReturn>? {
        logger.info { "cache test" }
        return stockInfoRepositorySupport.findAllByCondition(searchCorpCondition.name,searchCorpCondition.per)?.map { it.toStockInfoReturn() }
    }

}

// 테스트용
private fun slowQuery(seconds: Long) {
    try {
        Thread.sleep(seconds)
    } catch (e: InterruptedException) {
        throw IllegalStateException(e)
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