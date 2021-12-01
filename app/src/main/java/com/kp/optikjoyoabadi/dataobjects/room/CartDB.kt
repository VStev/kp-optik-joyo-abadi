package com.kp.optikjoyoabadi.dataobjects.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kp.optikjoyoabadi.model.Cart

@Database(entities = [Cart::class], version = 1, exportSchema = false)
abstract class CartDB: RoomDatabase() {
    abstract fun cartDao(): CartDAO
}