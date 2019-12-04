package com.gram.zoosick.config

import org.springframework.cache.CacheManager
import org.springframework.cache.ehcache.EhCacheCacheManager
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {

    @Bean
    fun cacheManager(): CacheManager {
        return EhCacheCacheManager(ehCacheManager().`object`!!)
    }
    
    @Bean
    fun ehCacheManager(): EhCacheManagerFactoryBean {
        return EhCacheManagerFactoryBean().apply {
            setConfigLocation(ClassPathResource("ehcache.xml"))
            setShared(true)
        }
    }


}