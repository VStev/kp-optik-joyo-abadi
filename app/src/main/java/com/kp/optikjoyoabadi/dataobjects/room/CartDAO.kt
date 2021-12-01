package com.kp.optikjoyoabadi.dataobjects.room

import androidx.room.*
import com.kp.optikjoyoabadi.model.Cart

@Dao
interface CartDAO {
//    @Query("SELECT * FROM cart")
//    fun getCart():
//    do we need rx or livedata enough?

    @Insert(onConflict =  OnConflictStrategy.REPLACE)
    fun insertCart(cart: Cart)

    @Delete
    fun deleteCart(cart: Cart)

    @Query("DELETE FROM cart")
    fun nukeCart()
}