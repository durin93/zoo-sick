package com.gram.zoosick.support

import mu.KLogging
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class RestTemplateHelper {

    companion object: KLogging()

    fun <T> get(url: String, responseType: Class<T>): ResponseEntity<T> {
        logger.info("============================")
        logger.info("get  [GET] $url")
        logger.info("============================")
        return RestTemplate().getForEntity(url,responseType)
    }

    fun <T> postWithFormData(url: String, request: Any? , responseType: Class<T>): ResponseEntity<T> {
        logger.info("============================")
        logger.info("postWithFormData  [POST] $url")
        logger.info { "request $request" }
        logger.info("============================")
        var headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_FORM_URLENCODED }
        return RestTemplate().postForEntity(url,HttpEntity(request,headers),responseType)
    }

}