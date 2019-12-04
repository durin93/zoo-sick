package com.gram.zoosick.domain.stock.entity

import com.gram.zoosick.domain.stock.HigherTheBetterDto
import javax.persistence.*

@Embeddable
data class HigherTheBetter(
        var netIncome: String? = null, //당기 순이익
        var roe: String? = null, // 최소한 정기예금 금리를 넘어야함. 당기순이익을 많이 내 효율적인 영업을 했다는 의미
        var eps: String? = null, // 1주당순이익
        var bps: String? = null // 순자산 / 발행주식
) {
    fun toHigherTheBetterDto(): HigherTheBetterDto {
        return HigherTheBetterDto(netIncome = netIncome, roe = roe, eps = eps, bps = bps)
    }
}