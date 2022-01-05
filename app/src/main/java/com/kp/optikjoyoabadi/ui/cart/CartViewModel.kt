package com.kp.optikjoyoabadi.ui.cart

import androidx.lifecycle.ViewModel
import com.kp.optikjoyoabadi.dataobjects.room.CartRepository
import com.kp.optikjoyoabadi.model.Cart

class CartViewModel(private val repository: CartRepository): ViewModel() {

    val cartItems = {
        repository.getCart()
    }

    fun delete(item: Cart){
        repository.deleteCart(item)
    }

}