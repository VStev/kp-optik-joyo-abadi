package com.kp.optikjoyoabadi.ui.productdetail

import androidx.lifecycle.ViewModel
import com.kp.optikjoyoabadi.dataobjects.room.CartRepository
import com.kp.optikjoyoabadi.model.Cart
import com.kp.optikjoyoabadi.model.Product

class ProductDetailViewModel(private val repository: CartRepository) : ViewModel() {

    fun addToCart(product: Product, note: String, quantity: Int) {
        val cart = Cart(
            product.productId,
            product.productName,
            product.category,
            product.shape,
            product.details,
            note,
            product.price,
            quantity,
            product.image_url
        )
        repository.insertCart(cart)
    }

}