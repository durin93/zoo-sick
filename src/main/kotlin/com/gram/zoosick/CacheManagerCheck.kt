package com.gram.zoosick

import mu.KLogging
import org.springframework.boot.CommandLineRunner
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Component

@Component
class CacheManagerCheck(val cacheManager: CacheManager) : CommandLineRunner {

    companion object : KLogging()

    @Throws(Exception::class)
    override fun run(vararg strings: String) {
        logger.info("\n\n" + "=========================================================\n"
                + "Using cache manager: " + this.cacheManager.javaClass.name + "\n"
                + "=========================================================\n\n")
    }
}