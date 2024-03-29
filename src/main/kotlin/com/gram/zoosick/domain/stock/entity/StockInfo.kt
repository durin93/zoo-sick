package com.gram.zoosick.domain.stock.entity

import com.gram.zoosick.domain.stock.StockInfoReturn
import com.gram.zoosick.support.BaseEntity
import javax.persistence.*

@Entity
data class StockInfo(

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0L,

        var name: String? = null, //종목명+코드

        var code: String, //코드

        var currentPrice: String? = null, //현재가

        var sales: String? = null, //매출

        @Embedded
        var lowerTheBetter: LowerTheBetter, //낮으면 좋은것

        @Embedded
        var higherTheBetter: HigherTheBetter, //높으면 좋은것

        var updatedStatus: Boolean = false

) : BaseEntity() {
    fun updateStatus() {
        this.updatedStatus = !this.updatedStatus
    }

    fun toStockInfoReturn(): StockInfoReturn {
        return StockInfoReturn(
                name = name,
                code = code,
                currentPrice = currentPrice,
                sales = sales,
                lowerTheBetterDto = lowerTheBetter?.toLowerTheBetterDto(),
                higherTheBetterDto = higherTheBetter?.toHigherTheBetterDto()
        )
    }

}