package com.example.bakeit.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.bakeit.API.ApiInterface
import com.example.bakeit.databinding.ActivityPersonalDetailsBinding
import com.example.bakeit.models.UpdateProfileResponse
import com.example.bakeit.models.UpdateUserRequest
import com.example.bakeit.models.User
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PersonalDetails : AppCompatActivity() {
    private lateinit var fetchedUser: User
    private lateinit var binding: ActivityPersonalDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fetchUser()
        binding.saveProceedBtn.setOnClickListener {
            saveAndProceed()
        }


        binding.updatePhoneBtn.setOnClickListener {
            startActivity(Intent(this,change_phone_number_activity::class.java))
        }


    }

    private fun fetchUser() {
        fetchedUser = UserPreferences(this@PersonalDetails).getUser()!!
        setExistingFields(fetchedUser)
    }

    private fun saveAndProceed() {
        if (binding.editTextName.text.isNotEmpty()) {

            val updateUser = UpdateUserRequest(
                binding.editTextName.text.toString(),
                binding.editTextEmail.text.toString(),
                binding.editTextPhone.text.toString()
            )

            val updateResp: Call<UpdateProfileResponse> =
                ApiInterface.ApiService.ApiInstance.updateUserProfile(
                        JwtManager.JwtManager.getToken(
                            this
                        ).toString()
                    , updateUser
                )

            updateResp.enqueue(object : Callback<UpdateProfileResponse> {
                override fun onResponse(
                    call: Call<UpdateProfileResponse>,
                    response: Response<UpdateProfileResponse>
                ) {
                    val updateResp: UpdateProfileResponse? = response.body()
                    if (updateResp != null && updateResp.success) {
                        Toast.makeText(this@PersonalDetails, "Profile Updated", Toast.LENGTH_SHORT).show()
                        updateUserPref()
                        finish()
                    } else {
                        Log.d("ExpressApiLog", "null")
                    }
                }

                override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                    Log.d("ExpressApiLog", "api error")
                }
            })
        } else {
            Toast.makeText(this, "Enter Required Fields", Toast.LENGTH_SHORT).show()
        }

    }

    private fun updateUserPref() {
        val exisitingUserPref = UserPreferences(this@PersonalDetails).getUser()
        exisitingUserPref!!.phone = binding.editTextPhone.text.toString()
        exisitingUserPref.email = binding.editTextEmail.text.toString()
        exisitingUserPref.username = binding.editTextName.text.toString()
        UserPreferences(this@PersonalDetails).setUser(exisitingUserPref)
    }

    private fun setExistingFields(user: User) {
        if (user.username != "") {
            binding.editTextName.setText(user.username)
        }
        if (user.email != "") {
            binding.editTextEmail.setText(user.email)
        }
        if (user.phone != ""){
            binding.editTextPhone.setText(user.phone)
        }
    }

    override fun onResume() {
        super.onResume()
        fetchUser()
    }
}