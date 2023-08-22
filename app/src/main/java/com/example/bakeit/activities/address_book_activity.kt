package com.example.bakeit.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.bakeit.R
import com.example.bakeit.databinding.ActivityAddressBookBinding
import com.example.bakeit.databinding.ActivityUserProfileBinding

class address_book_activity : AppCompatActivity() {
    private lateinit var binding: ActivityAddressBookBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addNewAddressButton.setOnClickListener {
            startActivity(Intent(this, UserLocationSelectorActivity::class.java))
        }



    }
}