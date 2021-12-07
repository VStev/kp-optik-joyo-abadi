package com.kp.optikjoyoabadi.ui.editprofile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kp.optikjoyoabadi.databinding.ActivityEditProfileBinding
import com.kp.optikjoyoabadi.databinding.ActivityPaymentDetailBinding

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showLayout()
    }

    private fun showLayout() {
        TODO("Not yet implemented")
    }
}