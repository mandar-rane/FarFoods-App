package com.example.bakeit.activities

import android.app.Application

class CartApp: Application() {
    val db by lazy{
        CartDatabase.getInstance(this)
    }
}