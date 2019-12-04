package com.gram.zoosick.config

import org.springframework.cache.annotation.CachingConfigurerSupport
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Configuration

@EnableCaching
@Configuration
class MemoryCacheConfig : CachingConfigurerSupport() {


}