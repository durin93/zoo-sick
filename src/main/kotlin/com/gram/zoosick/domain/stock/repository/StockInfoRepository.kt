package com.gram.zoosick.domain.stock.repository

import com.gram.zoosick.domain.stock.entity.StockInfo
import org.springframework.data.jpa.repository.JpaRepository

interface StockInfoRepository : JpaRepository<StockInfo,Long> {
}