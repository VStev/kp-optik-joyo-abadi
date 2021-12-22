package com.kp.optikjoyoabadi.ui.productdetail

import androidx.lifecycle.ViewModel
import com.kp.optikjoyoabadi.dataobjects.room.CartRepository
import com.kp.optikjoyoabadi.model.Cart
import com.kp.optikjoyoabadi.model.Product

class ProductDetailViewModel(private val repository: CartRepository): ViewModel() {

    fun addtoCart(product: Product){
        val cart = Cart(
                product.productId,
                product.productName,
                "ab",
                product.price,
                3,
                product.image_url
        )
        repository.insertCart(cart)
    }

}