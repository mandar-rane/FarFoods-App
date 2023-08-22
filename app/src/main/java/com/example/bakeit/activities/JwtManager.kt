package com.example.bakeit.activities

import android.content.Context

class JwtManager {

    object JwtManager{
        private const val PREFS_NAME = "jwt_prefs"
        private const val AUTH_TOKEN_KEY = "auth_token"


        fun saveToken(context: Context, token: String) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(AUTH_TOKEN_KEY, token)
                .apply()
        }

        fun getToken(context: Context): String? {
            return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(AUTH_TOKEN_KEY, null)
        }
    }
}