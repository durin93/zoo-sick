package com.gram.zoosick.domain.stock.repository

import com.gram.zoosick.domain.stock.SearchCorpCondition
import com.gram.zoosick.domain.stock.entity.QStockInfo
import com.gram.zoosick.domain.stock.entity.QStockInfo.stockInfo
import com.gram.zoosick.domain.stock.entity.StockInfo
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.thymeleaf.util.StringUtils
import java.time.LocalDateTime


interface StockInfoRepository : JpaRepository<StockInfo, Long>, QuerydslPredicateExecutor<StockInfo> {
    @Query("delete from StockInfo s where s.id in :ids")
    fun deleteAllByIdInQuery(@Param("ids") ids: List<Long>)

    fun findByUpdatedDateBefore(now: LocalDateTime): List<StockInfo>?
}

@Repository
class StockInfoRepositorySupport(
        val query: JPAQueryFactory
) : QuerydslRepositorySupport(StockInfo::class.java) {

    fun findByName(name: String): StockInfo? {
        return query.selectFrom(QStockInfo.stockInfo)
                .where(QStockInfo.stockInfo.name.eq(name))
                .fetchOne() //UniqueResult
    }

    fun findAllByCondition(name: String?, per: String?): List<StockInfo>? {
        return query.selectFrom(stockInfo)
                .where(eqName(name),eqPer(per))
                .fetch()
    }

    private fun eqName(name: String?): BooleanExpression? {
        if(StringUtils.isEmpty(name)){
            return null
        }
        return stockInfo.name.eq(name)
    }

    private fun eqPer(per: String?): BooleanExpression? {
        if(StringUtils.isEmpty(per)){
            return null
        }
        return stockInfo.lowerTheBetter.per.lt(per)
    }

}
