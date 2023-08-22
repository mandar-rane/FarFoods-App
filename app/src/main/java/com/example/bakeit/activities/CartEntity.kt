package com.example.bakeit.activities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart-table")
data class CartEntity(
    @PrimaryKey
    val pid: String = "",
    val itemName: String = "",
    val itemPrice: Double = 0.0,
    val itemImg: String = "",
    val itemShopId: String = "",
    var totalItemPrice: Double = 0.0,
    var quantity: Int = 0
)
