package com.gram.zoosick.domain.stock.schedule

import java.text.SimpleDateFormat
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.*


@Component
class Scheduler {

    @Scheduled(cron = "0 35 10 * * *")
    fun tempCronJob() {
        val executeDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        println("${Thread.currentThread().name} tempCronJob 10:35:00 ${executeDate}")
    }

   /* @Scheduled(fixedDelay = 1000) 1 seconds
    fun scheduleFixedRateTask() {
        println("${Thread.currentThread().name} Fixed rate task - ${System.currentTimeMillis() / 1000}")
    }*/
}