package com.example.bakeit.activities

import android.content.Context
import android.content.SharedPreferences
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.example.bakeit.R
import com.example.bakeit.databinding.ActivityPaymentOptionBinding

class PaymentOptionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentOptionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentOptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val paymentModePreference: SharedPreferences = getSharedPreferences("paymentModePrefs", Context.MODE_PRIVATE)
        val editor = paymentModePreference.edit()

        binding.gpayOption.setOnClickListener {
            editor.putString("payment_mode", "gpay")
            editor.apply()
            finish()
        }

        binding.codOption.setOnClickListener {
            editor.putString("payment_mode", "cod")
            editor.apply()
            finish()
        }

    }
}