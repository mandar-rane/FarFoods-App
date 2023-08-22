package com.example.bakeit.models

data class Products(
    var _id: String? = null,
    var prodname: String? = null,
    var prodprice: Double? = null,
    var prodimages: List<String>? = null,
    var veg: String? = null,
    var prodcategory: String? = null,
    var proddescription: String? = null
)
