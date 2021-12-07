package com.kp.optikjoyoabadi.ui.addaddress

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kp.optikjoyoabadi.databinding.ActivityAddAddressBinding

class AddAddressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddAddressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showLayout()
    }

    private fun showLayout() {
        TODO("Not yet implemented")
    }
}