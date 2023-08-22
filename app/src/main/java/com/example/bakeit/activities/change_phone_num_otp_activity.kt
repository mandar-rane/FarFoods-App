package com.example.bakeit.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.bakeit.API.ApiInterface
import com.example.bakeit.R
import com.example.bakeit.models.SendOtpResponse
import com.example.bakeit.models.loginApiResponse
import com.example.bakeit.models.verifyUserPhone
import com.google.android.play.core.integrity.p
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class change_phone_num_otp_activity : AppCompatActivity() {
    private lateinit var OTP : String
    private lateinit var phoneNumber: String
    private var counter = 60
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_phone_num_otp)


        val etOTP = findViewById<EditText>(R.id.updatePhoneOtpET)
        val verifyOTP = findViewById<Button>(R.id.updatePhoneSendOtpButton)
        phoneNumber = intent.getStringExtra("phoneNumber").toString()

        verifyOTP.setOnClickListener {
            val entered_otp = etOTP.text.toString()
            verifyPhoneWithOtp(phoneNumber, entered_otp)
        }

    }

    private fun verifyPhoneWithOtp(phoneNumber: String, enteredOtp: String) {
        val verifyOtpResp = ApiInterface.ApiService.ApiInstance.verifyPhoneUpdateOtp(JwtManager.JwtManager.getToken(this).toString(),verifyUserPhone(phoneNumber, enteredOtp))
        verifyOtpResp.enqueue(object: Callback<SendOtpResponse> {
            override fun onResponse(call: Call<SendOtpResponse>, response: Response<SendOtpResponse>) {
                val verifyOtpResp: SendOtpResponse? = response.body()
                if (verifyOtpResp != null){
                    if (verifyOtpResp!!.success) {
                        Toast.makeText(this@change_phone_num_otp_activity, verifyOtpResp.message, Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@change_phone_num_otp_activity, verifyOtpResp.message, Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Log.d("otpApiLog", "null")
                    Toast.makeText(this@change_phone_num_otp_activity, "null", Toast.LENGTH_SHORT).show()
                }


            }
            override fun onFailure(call: Call<SendOtpResponse>, t: Throwable) {
                Log.d("verifyApiLog","api error")
            }
        })
    }
}