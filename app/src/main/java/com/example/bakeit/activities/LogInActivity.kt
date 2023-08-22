package com.example.bakeit.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import com.example.bakeit.API.ApiInterface
import com.example.bakeit.R
import com.example.bakeit.models.SendOtpResponse
import com.example.bakeit.models.ShopResponse
import com.example.bakeit.models.User
import com.example.bakeit.models.loginUserPhone
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.safetynet.SafetyNet
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class LogInActivity : AppCompatActivity() {
    //    private lateinit var googleSignInClient: GoogleSignInClient
//    private lateinit var auth: FirebaseAuth
    private lateinit var phoneNumber: String
//    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
//    private var storedVerificationId: String? = ""
//    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)


        findViewById<Button>(R.id.continue_btn_login).setOnClickListener {
            phoneNumber = findViewById<EditText>(R.id.phone_et).text.toString()
            if (phoneNumber != "" && phoneNumber.length == 10) {
                startPhoneNumberVerification(phoneNumber)
            } else {
                Toast.makeText(this, "Enter Phone Number", Toast.LENGTH_LONG).show()
            }
        }

//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken("330223101649-2bou9bn58m0eso0089171l7aqo16prh2.apps.googleusercontent.com")
//            .requestEmail().build()
//        googleSignInClient = GoogleSignIn.getClient(this, gso)
//
//        val googleSignInIntent = googleSignInClient.signInIntent
//        val getResult =
//            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//                if (it.resultCode == Activity.RESULT_OK) {
//                    val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
//                    try {
//                        val account = task.getResult(ApiException::class.java)
//                        Log.d(ContentValues.TAG, "firebaseAuthWithGoogle" + account.id)
//                        firebaseAuthWithGoogle(account.idToken)
//                    } catch (e: ApiException) {
//                        Log.d(ContentValues.TAG, "Google sign in Failed")
//                    }
//                }
//            }
//        findViewById<CardView>(R.id.google_login_btn).setOnClickListener {
//            googleSignIn(getResult, googleSignInIntent )
//        }
    }

//    private fun googleSignIn(getResult: ActivityResultLauncher<Intent>, googleSignInIntent: Intent) {
//        getResult.launch(googleSignInIntent)
//
//    }

//    private fun firebaseAuthWithGoogle(idToken: String?) {
//        val credent = GoogleAuthProvider.getCredential(idToken, null)
//        auth.signInWithCredential(credent).addOnCompleteListener(this) { task ->
//            if (task.isSuccessful) {
//                Log.d(ContentValues.TAG, "signInWithCredential: Success")
//                val user = auth.currentUser
//                updateUI(user)
//            } else {
//                Log.w(TAG, "signInWithCredential:failure", task.exception)
//            }
//        }
//    }

//    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
//        auth.signInWithCredential(credential)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    Log.d(TAG, "signInWithCredential:success")
//                    val user = task.result?.user
//                    updateUI(user)
//                } else {
//                    // Sign in failed, display a message and update the UI
//                    Log.w(TAG, "signInWithCredential:failure", task.exception)
//                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
//                        // The verification code entered was invalid
//                    }
//                    // Update UI
//                }
//            }
//    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        val sendOtpResp =
            ApiInterface.ApiService.ApiInstance.sendLoginOtp(loginUserPhone(phoneNumber))
        sendOtpResp.enqueue(object : Callback<SendOtpResponse> {
            override fun onResponse(
                call: Call<SendOtpResponse>,
                response: Response<SendOtpResponse>
            ) {
                val sendOtpResp: SendOtpResponse? = response.body()
                if (sendOtpResp != null && sendOtpResp.success) {
                    Log.d("otpApiLog", sendOtpResp.message)
                } else {
                    Log.d("otpApiLog", "null")
                }
            }

            override fun onFailure(call: Call<SendOtpResponse>, t: Throwable) {
                Log.d("otpApiLog", "otp api error ${t}")
            }
        })
        val otp_intent = Intent(this@LogInActivity, OTPactivity::class.java)
        otp_intent.putExtra("phoneNumber", phoneNumber)
        startActivity(otp_intent)
    }

//    fun updateUI(user: FirebaseUser?) {
//        val logInToMain = Intent(this, MainActivity::class.java)
//        logInToMain.putExtra("user",user)
//        startActivity(logInToMain)
//    }

    override fun onStart() {
        super.onStart()

        val token = JwtManager.JwtManager.getToken(this)

        if (token != null) {
            val logInToMain = Intent(this, MainActivity::class.java)
            startActivity(logInToMain)
        }


    }

//    companion object {
//        const val TAG = "PhoneAuthActivity"
//    }
}