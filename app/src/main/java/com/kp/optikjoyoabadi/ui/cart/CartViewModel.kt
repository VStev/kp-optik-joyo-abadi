package com.kp.optikjoyoabadi.ui.cart

import androidx.lifecycle.ViewModel
import com.kp.optikjoyoabadi.dataobjects.room.CartRepository

class CartViewModel(private val repository: CartRepository): ViewModel() {

    val cartItems = {
        repository.getCart()
    }

}