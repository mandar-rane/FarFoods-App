package com.example.bakeit.activities

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit
import com.example.bakeit.models.User
import com.google.gson.Gson

class UserPreferences(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("user_pref", MODE_PRIVATE)
    private val gson = Gson()

    fun getUser(): User? {
        val userJson = sharedPreferences.getString("user_data", null)
        return if (userJson != null) {
            gson.fromJson(userJson, User::class.java)
        } else {
            null
        }
    }

    fun setUser(user: User) {
        val userJson = gson.toJson(user)
        sharedPreferences.edit{
            putString("user_data", userJson)
        }
    }
}