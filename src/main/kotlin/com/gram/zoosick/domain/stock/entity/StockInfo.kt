package com.gram.zoosick.domain.stock.entity

import javax.persistence.*

@Entity
data class StockInfo(
        @Id
        @Column(name="id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,

        var per: String? = null
)