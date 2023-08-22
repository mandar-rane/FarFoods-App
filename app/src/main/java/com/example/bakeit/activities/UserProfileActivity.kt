package com.example.bakeit.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.bakeit.R
import com.example.bakeit.databinding.ActivityUserProfileBinding
import com.example.bakeit.models.User
import com.example.bakeit.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class UserProfileActivity : AppCompatActivity() {
    private lateinit var user: User
    private lateinit var binding: ActivityUserProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = UserPreferences(this@UserProfileActivity).getUser()!!

        setUserProfilePreview(user)


        /*binding.editLocBtn.setOnClickListener {
            showChangeLocPopup()
        }*/

        binding.Orders.setOnClickListener {
            startActivity(Intent(this, OrdersActivity::class.java))
        }


        binding.LogOutBtn.setOnClickListener {

        }

        binding.userProfilePreviewCard.setOnClickListener {
            startActivity(Intent(this,PersonalDetails::class.java))
        }

        //user = FirestoreClass().retrieveUser()!!
        binding.AddressBook.setOnClickListener {
            startActivity(Intent(this, address_book_activity::class.java))
        }

    }

    private fun setUserProfilePreview(user: User) {

    }


    /*
    @SuppressLint("InflateParams")
    private fun showChangeLocPopup() {
        val popupBinding = layoutInflater.inflate(R.layout.change_loc_profile_popup, null)
        val changeLoc = Dialog(this)
        changeLoc.setContentView(R.layout.change_loc_profile_popup)
        changeLoc.setCancelable(true)
        changeLoc.show()
        val change_loc_et = changeLoc.findViewById<EditText>(R.id.change_address_popup_et)
        val tv_address = findViewById<TextView>(R.id.delivery_address_profile_page_tv)
        changeLoc.findViewById<Button>(R.id.change_address_popup_save_btn).setOnClickListener {
            if (change_loc_et.text.toString().trim { it <= ' ' } == "") {
                Toast.makeText(this, "Please Enter Address", Toast.LENGTH_LONG).show()
            } else {
                tv_address.text = change_loc_et.text.toString().trim { it <= ' ' }

                changeLoc.dismiss()
            }

        }
    }*/
}



