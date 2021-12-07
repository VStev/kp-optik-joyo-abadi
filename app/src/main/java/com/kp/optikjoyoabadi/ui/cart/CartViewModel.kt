package com.kp.optikjoyoabadi.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kp.optikjoyoabadi.dataobjects.room.CartRepository
import com.kp.optikjoyoabadi.model.Cart

class CartViewModel(private val repository: CartRepository): ViewModel() {

    private val cartItems = {
        repository.getCart()
    }

}