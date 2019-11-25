package com.gram.zoosick.domain.stock.service

import com.gram.zoosick.domain.stock.*
import mu.KLogging
import org.jsoup.Jsoup
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.function.Supplier

@Service
class StockService(
        val stockApiManager: StockApiManager
) {

    companion object : KLogging()

    val executors: ExecutorService = Executors.newFixedThreadPool(1000)

    fun getAllCorporationsDetail(): List<CoreInfo> {
        return getAllCorporationsCodes()?.let {
            getDetailCorpDetails(it)
        } ?: emptyList()
    }

    private fun getAllCorporationsCodes(): List<String>? {
        val response = stockApiManager.getAllCorporations(CorpSearchRequest().toParams())
        val stockCodes: List<String>? = Jsoup.parseBodyFragment(response.toString()).select("td[style*=@]").text().split(" ")
        return stockCodes ?: emptyList()
    }

    fun asyncCreateCorpDetail(code: String): CompletableFuture<CoreInfo> {
        return CompletableFuture.supplyAsync(Supplier {
            stockApiManager.getCorporationsDetails(code)
        }, executors).thenApply { response ->
            val parsedResponse = Jsoup.parseBodyFragment(response.toString())
            val perTable = parsedResponse.getElementsByClass("per_table") //투자정보에 위
            val sectionCopAnalysis = parsedResponse.getElementsByClass("section cop_analysis")?.let { it.first() }//기업실적분석
            val sectionTradeCompare = parsedResponse.getElementsByClass("section trade_compare")?.let { it.first() } //동일업종비
            CoreInfo(
                    name = sectionTradeCompare?.let{it.getElementsByClass("h_th2 th_cop_comp1").next().select("a").text()},
                    code = code,
                    currentPrice = sectionTradeCompare?.let{it.getElementsByClass("h_th2 th_cop_comp2").next().text()},
                    sales = sectionTradeCompare?.let{it.getElementsByClass("h_th2 th_cop_comp7").next().text()},
                    lowerTheBetter = LowerTheBetter(
                            per = perTable.select("em#krx_per").text(),
                            pbr = perTable.select("em#_pbr").text(),
                            debtRatio = sectionCopAnalysis?.let{it.getElementsByClass("h_th2 th_cop_anal14").nextAll().not(".null").last().text()}),
                    higherTheBetter = HigherTheBetter(
                            netIncome = sectionTradeCompare?.let{it.getElementsByClass("h_th2 th_cop_comp10").next().text()},
                            roe = sectionTradeCompare?.let{it.getElementsByClass("h_th2 th_cop_comp12").next().text()},
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

        /*val response = kindApiManager.getCorporationsDetails(code)
        val perTable = Jsoup.parseBodyFragment(response.toString()).getElementsByClass("per_table")
        return CoreInfo(
                code = code,
                per = perTable.select("em#_per").text(),
                krxPer = perTable.select("em#krx_per").text(),
                pbr = perTable.select("em#_pbr").text()
        )*/
    }

    private fun getDetailCorpDetails(stockCodes: List<String>): List<CoreInfo> {
        return stockCodes.map {
            asyncCreateCorpDetail(it)
        }.allOfJoin()
    }

    fun searchCorpsByCondition(searchCorpCondition: SearchCorpCondition): List<CoreInfo>? {
        val stockCodes = getAllCorporationsCodes()
        return stockCodes?.let { getDetailCorpDetails(it) }
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