package com.example.bakeit.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.example.bakeit.API.ApiInterface
import com.example.bakeit.databinding.ActivityChangePhoneNumberBinding
import com.example.bakeit.models.SendOtpResponse
import com.example.bakeit.models.loginUserPhone
import org.checkerframework.common.returnsreceiver.qual.This
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class change_phone_number_activity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePhoneNumberBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePhoneNumberBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.sendOtpBtn.setOnClickListener {
            validatePhoneField()
        }

    }

    private fun validatePhoneField() {
            val phoneNumber = binding.phoneEt.text.toString()
            val phoneRegex = "^[+]?[0-9]{10}\$".toRegex()
            if (phoneNumber.matches(phoneRegex)) {
                  otpApiRequest(phoneNumber)
//                startActivity(Intent(this, change_phone_num_otp_activity::class.java))
            }else{
                Toast.makeText(this,"Enter valid phone number", Toast.LENGTH_SHORT).show()
            }
    }

    private fun otpApiRequest(phoneNumber: String) {
        Log.d("otpApiLogphone", phoneNumber)
        val otpResp = ApiInterface.ApiService.ApiInstance.sendPhoneUpdateOtp(JwtManager.JwtManager.getToken(this).toString(), loginUserPhone(phoneNumber))
        otpResp.enqueue(object : Callback<SendOtpResponse> {
            override fun onResponse(call: Call<SendOtpResponse>, response: Response<SendOtpResponse>) {
                val otpResp: SendOtpResponse? = response.body()
                Log.d("otpApiLogi", response.body().toString())

                if (otpResp != null) {

                    if (otpResp.success) {

                        Log.d("otpApiLog", otpResp.message)
                        Toast.makeText(this@change_phone_number_activity, otpResp.message, Toast.LENGTH_SHORT).show()
                        val otpVerifIntent = Intent(this@change_phone_number_activity, change_phone_num_otp_activity::class.java)
                        otpVerifIntent.putExtra("phoneNumber", phoneNumber)
                        startActivity(otpVerifIntent)

                    } else {
                        Log.d("otpApiLog", otpResp.message)
                        Toast.makeText(this@change_phone_number_activity, otpResp.message, Toast.LENGTH_SHORT).show()

                    }

                }else{
                    Log.d("otpApiLog", "null")
                }
            }
            override fun onFailure(call: Call<SendOtpResponse>, t: Throwable) {
                Log.d("otpApiLog", "api error")
            }
        })
    }
}