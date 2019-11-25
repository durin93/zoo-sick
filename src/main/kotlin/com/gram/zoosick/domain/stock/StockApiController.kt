package com.gram.zoosick.domain.stock

import com.gram.zoosick.domain.stock.service.StockService
import mu.KLogging
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/stock")
class StockApiController(val stockService: StockService) {
    companion object : KLogging()

    @GetMapping("/corps/core")
    fun getAllCorporations(): List<CoreInfo> {
        return stockService.getAllCorporations()
    }

    @GetMapping("")
    fun getDetailByCorpCode(code: String): CoreInfo? {
        return stockService.getDetailCorpDetail(code)?.get()
    }

    @GetMapping("/search")
    fun searchSimilarCorp(@RequestBody searchCorpCondition: SearchCorpCondition): List<CoreInfo>? {
        return stockService.searchCorpsByCondition(searchCorpCondition)
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
data class CoreInfo(
        var name: String? = null, //종목명+코드
        var code: String? = null, //코드
        var currentPrice: String? = null, //현재가
        var sales: String? = null, //매출
        var lowerTheBetter: LowerTheBetter, //낮으면 좋은것
        var higherTheBetter: HigherTheBetter //높으면 좋은것
)

data class LowerTheBetter(
        var per: String? = null, //10이하면 보통, 6~4정도로 낮으면 좋다. 예외있음 //낮을수록 주식가격 상승 가능성높
        var pbr: String? = null, //낮을수록 자산가치가 저평가 되어있다는 뜻,
        var debtRatio: String? = null //부채비울 (부채/자본 x 100%) 가장최근분기실적으로 가져왓

)

data class HigherTheBetter(
        var netIncome: String? = null, //당기 순이익
        var roe: String? = null, // 최소한 정기예금 금리를 넘어야함. 당기순이익을 많이 내 효율적인 영업을 했다는 의미
        var eps: String? = null, // 1주당순이익
        var bps: String? = null // 순자산 / 발행주식
)