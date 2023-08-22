package com.example.bakeit.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.bumptech.glide.Glide.init
import com.example.bakeit.API.ApiInterface
import com.example.bakeit.R

import com.example.bakeit.models.*
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class OTPactivity : AppCompatActivity() {
    private lateinit var resendOTP: Button
    private lateinit var OTP : String
    private lateinit var phoneNumber: String
    private var counter = 60
    private lateinit var inputOTP1: EditText
    private lateinit var inputOTP2: EditText
    private lateinit var inputOTP3: EditText
    private lateinit var inputOTP4: EditText
    private lateinit var inputOTP5: EditText
    private lateinit var inputOTP6: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otpactivity)

        init()
        addTextChangeListener()





//        val etOTP = findViewById<EditText>(R.id.editTextOTP)
        val verifyOTP = findViewById<Button>(R.id.OTPbutton)
        resendOTP = findViewById<Button>(R.id.RESENDbutton)
        OTP =  intent.getStringExtra("OTP").toString()

        phoneNumber = intent.getStringExtra("phoneNumber").toString()

        verifyOTP.setOnClickListener {
            val entered_otp = (inputOTP1.text.toString() + inputOTP2.text.toString() + inputOTP3.text.toString()
                    + inputOTP4.text.toString() + inputOTP5.text.toString() + inputOTP6.text.toString())
            if (entered_otp.isNotEmpty()) {
                if (entered_otp.length == 6) {
                    signInWithPhoneAuthCredential(phoneNumber, entered_otp)
                } else {
                    Toast.makeText(this, "Please Enter Correct OTP", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please Enter OTP", Toast.LENGTH_SHORT).show()
            }
        }

        resendOTP.setOnClickListener {
            resendVerificationCode()
            resendOTPTvVisibility()
        }
    }


    private fun resendOTPTvVisibility() {
        inputOTP1.setText("")
        inputOTP2.setText("")
        inputOTP3.setText("")
        inputOTP4.setText("")
        inputOTP5.setText("")
        inputOTP6.setText("")

        val timer = object: CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                resendOTP.alpha = .5f
                resendOTP.isClickable = false
                resendOTP.text = "resend in ${counter}"
                counter--
            }
            override fun onFinish() {
                resendOTP.alpha = 1f
                resendOTP.isClickable = true
                counter = 60
                resendOTP.text = "RESEND"
            }
        }
        timer.start()
    }

    private fun signInWithPhoneAuthCredential(phone: String, OTP: String) {
        val loginResp: Call<loginApiResponse> = ApiInterface.ApiService.ApiInstance.verifyOtp(verifyUserPhone(phone, OTP))

        loginResp.enqueue(object: Callback<loginApiResponse> {
            override fun onResponse(call: Call<loginApiResponse>, response: Response<loginApiResponse>) {
                val loginResp: loginApiResponse? = response.body()
                if (loginResp != null){
                    if (loginResp.success == "true"){
                        JwtManager.JwtManager.saveToken(this@OTPactivity, loginResp.token)
                        updateUI()
                    }
                }else{
                    Log.d("verifyApiLog","null")
                }
            }
            override fun onFailure(call: Call<loginApiResponse>, t: Throwable) {
                Log.d("verifyApiLog","api error")
            }
        })
    }

    fun updateUI(){
        val otpToMain = Intent(this, MainActivity::class.java)
        startActivity(otpToMain)
    }

    private fun resendVerificationCode() {
        val sendOtpResp = ApiInterface.ApiService.ApiInstance.sendLoginOtp(loginUserPhone(phoneNumber))
    }

    private fun addTextChangeListener() {
        inputOTP1.addTextChangedListener(EditTextWatcher(inputOTP1))
        inputOTP2.addTextChangedListener(EditTextWatcher(inputOTP2))
        inputOTP3.addTextChangedListener(EditTextWatcher(inputOTP3))
        inputOTP4.addTextChangedListener(EditTextWatcher(inputOTP4))
        inputOTP5.addTextChangedListener(EditTextWatcher(inputOTP5))
        inputOTP6.addTextChangedListener(EditTextWatcher(inputOTP6))
    }

    private fun init() {
        inputOTP1 = findViewById(R.id.otpEditText1)
        inputOTP2 = findViewById(R.id.otpEditText2)
        inputOTP3 = findViewById(R.id.otpEditText3)
        inputOTP4 = findViewById(R.id.otpEditText4)
        inputOTP5 = findViewById(R.id.otpEditText5)
        inputOTP6 = findViewById(R.id.otpEditText6)
    }



    inner class EditTextWatcher(private val view: View) : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {

            val text = p0.toString()
            when (view.id) {
                R.id.otpEditText1 -> if (text.length == 1) inputOTP2.requestFocus()
                R.id.otpEditText2 -> if (text.length == 1) inputOTP3.requestFocus() else if (text.isEmpty()) inputOTP1.requestFocus()
                R.id.otpEditText3 -> if (text.length == 1) inputOTP4.requestFocus() else if (text.isEmpty()) inputOTP2.requestFocus()
                R.id.otpEditText4 -> if (text.length == 1) inputOTP5.requestFocus() else if (text.isEmpty()) inputOTP3.requestFocus()
                R.id.otpEditText5 -> if (text.length == 1) inputOTP6.requestFocus() else if (text.isEmpty()) inputOTP4.requestFocus()
                R.id.otpEditText6 -> if (text.isEmpty()) inputOTP5.requestFocus()

            }
        }

    }


}