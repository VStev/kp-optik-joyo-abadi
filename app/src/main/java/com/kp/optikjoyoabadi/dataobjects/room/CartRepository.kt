package com.kp.optikjoyoabadi.dataobjects.room

import androidx.lifecycle.LiveData
import com.kp.optikjoyoabadi.executeThread
import com.kp.optikjoyoabadi.model.Cart

class CartRepository (private val dao: CartDAO) {
//
//    companion object {
//        @Volatile
//        private var instance: CartRepository? = null
////        private const val PAGE_SIZE = 10
//
//        fun getInstance(context: Context): CartRepository? {
//            return instance ?: synchronized(CartRepository::class.java) {
//                if (instance == null) {
//                    val database = CartDB.getInstance(context)
//                    instance = CartRepository(database.cartDao())
//                }
//                return instance
//            }
//        }
//    }

    fun getCart(): LiveData<List<Cart>> {
        return dao.getCart()
    }

    fun insertCart(cart: Cart) = executeThread{
        dao.insertCart(cart)
    }

    fun deleteCart(cart: Cart) = executeThread {
        dao.deleteCart(cart)
    }

    fun nukeCart() = executeThread{
        dao.nukeCart()
    }
}