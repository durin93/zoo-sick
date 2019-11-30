package com.gram.zoosick.domain.stock

import com.gram.zoosick.domain.stock.entity.HigherTheBetter
import com.gram.zoosick.domain.stock.entity.LowerTheBetter
import com.gram.zoosick.domain.stock.entity.StockInfo
import com.gram.zoosick.domain.stock.service.StockService
import mu.KLogging
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/stock")
class StockApiController(val stockService: StockService) {
    companion object : KLogging()

    @GetMapping("/corps/core")
    fun getAllCorporationsDetail(): List<StockInfoReturn> {
        val coreInfo = stockService.getAllCorporationsDetail()
        logger.info { "size ${coreInfo.size}" }
        return coreInfo
    }

    @GetMapping("")
    fun getDetailByCorpCode(code: String): StockInfoReturn? {
        return stockService.asyncCreateCorpDetail(code)?.get()
    }

    @GetMapping("/search")
    fun searchSimilarCorp(@RequestBody searchCorpCondition: SearchCorpCondition): List<StockInfoReturn>? {
        return stockService.searchCorpsByCondition(searchCorpCondition)
    }

    @GetMapping("/test")
    fun test(){
        stockService.updateAllStockInfo()
    }

}

data class SearchCorpCondition(
        var per: String? = null
)

data class CorpSearchRequest(
        var method: String = "download",
        var orderMode: String = "1",
        var orderStat: String = "D",
        var searchType: String = "13",
        var fiscalYearEnd: String = "all",
        var location: String = "all"
) {
    fun toParams(): LinkedMultiValueMap<String, String> {
        var params = LinkedMultiValueMap<String, String>()
        params.add("method", method)
        params.add("orderMode", orderMode)
        params.add("orderStat", orderStat)
        params.add("searchType", searchType)
        params.add("fiscalYearEnd", fiscalYearEnd)
        params.add("location", location)
        return params
    }
}

//부채비율을 제외한 나머지값들은 동입얼종비교(가장최근분기 데이터를가져옴)
data class StockInfoReturn(
        var name: String? = null, //종목명+코드
        var code: String, //코드
        var currentPrice: String? = null, //현재가
        var sales: String? = null, //매출
        var lowerTheBetterDto: LowerTheBetterDto?, //낮으면 좋은것
        var higherTheBetterDto: HigherTheBetterDto? //높으면 좋은것
) {
    fun toStockInfo(): StockInfo {
        return StockInfo(
                name=name,
                code=code,
                currentPrice = currentPrice,
                sales = sales,
                lowerTheBetter = lowerTheBetterDto?.toLowerTheBetter() ?: LowerTheBetter(),
                higherTheBetter = higherTheBetterDto?.toHigherTheBetter() ?: HigherTheBetter()
        )
    }
}

data class LowerTheBetterDto(
        var per: String? = null, //10이하면 보통, 6~4정도로 낮으면 좋다. 예외있음 //낮을수록 주식가격 상승 가능성높
        var pbr: String? = null, //낮을수록 자산가치가 저평가 되어있다는 뜻,
        var debtRatio: String? = null //부채비울 (부채/자본 x 100%) 가장최근분기실적으로 가져왓
) {
    fun toLowerTheBetter(): LowerTheBetter {
        return LowerTheBetter(per = per, pbr = pbr, debtRatio = debtRatio)
    }
}

data class HigherTheBetterDto(
        var netIncome: String? = null, //당기 순이익
        var roe: String? = null, // 최소한 정기예금 금리를 넘어야함. 당기순이익을 많이 내 효율적인 영업을 했다는 의미
        var eps: String? = null, // 1주당순이익
        var bps: String? = null // 순자산 / 발행주식
){
    fun toHigherTheBetter(): HigherTheBetter {
        return HigherTheBetter(netIncome = netIncome, roe = roe, eps = eps, bps = bps)
    }
}