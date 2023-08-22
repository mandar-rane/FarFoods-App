package com.example.bakeit.models

data class Order(
   val _id: String,
   val orderitems: List<OrderItem>,
   val totalprice: Double,
   val orderstatus: String,
   val paidat: String
)
