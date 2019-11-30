package com.gram.zoosick.domain.stock

import com.gram.zoosick.support.RestTemplateHelper
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import java.lang.Thread.sleep

@Component
class StockApiManager(
        val restTemplateHelper: RestTemplateHelper
) {
    private final val GET_ALL_COPERATIONS = "http://kind.krx.co.kr/corpgeneral/corpList.do"
    private final val GET_COPERATIONS_DETAILS = "https://finance.naver.com/item/main.nhn"

    fun getAllCorporations(request: Any?): ResponseEntity<String> {
        return restTemplateHelper.postWithFormData(GET_ALL_COPERATIONS, request, String::class.java)
    }

    fun getCorporationsDetails(code: String): ResponseEntity<String> {
        val requestUrl = UriComponentsBuilder.fromHttpUrl(GET_COPERATIONS_DETAILS).queryParam("code",code)
        return restTemplateHelper.get(requestUrl.toUriString(), String::class.java)
    }

}