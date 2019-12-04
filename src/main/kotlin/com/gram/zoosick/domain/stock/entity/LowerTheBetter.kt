package com.gram.zoosick.domain.stock.entity

import com.gram.zoosick.domain.stock.LowerTheBetterDto
import java.io.Serializable
import javax.persistence.*

@Embeddable
data class LowerTheBetter(
        var per: String? = null, //10이하면 보통, 6~4정도로 낮으면 좋다. 예외있음 //낮을수록 주식가격 상승 가능성높
        var pbr: String? = null, //낮을수록 자산가치가 저평가 되어있다는 뜻,
        var debtRatio: String? = null //부채비울 (부채/자본 x 100%) 가장최근분기실적으로 가져왓
): Serializable {
    fun toLowerTheBetterDto(): LowerTheBetterDto {
        return LowerTheBetterDto(per = per, pbr = pbr, debtRatio = debtRatio)
    }
}