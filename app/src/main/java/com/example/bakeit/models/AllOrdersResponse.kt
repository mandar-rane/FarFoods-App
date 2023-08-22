package com.example.bakeit.models

data class AllOrdersResponse(
    val success: String,
    val orders: List<Order>
)
