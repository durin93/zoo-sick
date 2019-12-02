package com.gram.zoosick.domain.stock.repository

import com.gram.zoosick.domain.stock.entity.StockInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface StockInfoRepository : JpaRepository<StockInfo, Long> {
    @Query("delete from StockInfo s where s.id in :ids")
    fun deleteAllByIdInQuery(@Param("ids") ids: List<Long>)

    fun findByUpdatedDateBefore(now: LocalDateTime): List<StockInfo>?
}