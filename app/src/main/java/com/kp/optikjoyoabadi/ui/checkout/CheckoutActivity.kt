package com.kp.optikjoyoabadi.ui.checkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kp.optikjoyoabadi.databinding.ActivityCheckoutBinding

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding

    //TODO(check address, change address, insert to firestore transaction+transactiondetail)
    //TODO(shipping fee, city surabaya region east java = 10000, any java region = 15000, else 25000. only regular shipping)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}