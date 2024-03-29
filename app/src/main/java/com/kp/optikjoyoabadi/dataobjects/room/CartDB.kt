package com.kp.optikjoyoabadi.dataobjects.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kp.optikjoyoabadi.model.Cart

@Database(entities = [Cart::class], version = 4, exportSchema = false)
abstract class CartDB: RoomDatabase() {
    abstract fun cartDao(): CartDAO
//
//    companion object {
//        @Volatile
//        private var instance: CartDB? = null
//
//        fun getInstance(context: Context): CartDB {
//            return synchronized(this){
//                instance ?: Room.databaseBuilder(context, CartDB::class.java, "cart.db")
//                    .build()
//            }
//        }
//    }
}