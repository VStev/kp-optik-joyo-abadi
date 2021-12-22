package com.kp.optikjoyoabadi.di

import androidx.room.Room
import com.kp.optikjoyoabadi.dataobjects.room.CartDB
import com.kp.optikjoyoabadi.ui.cart.CartViewModel
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@JvmField
val dbModule = module{
    factory { get<CartDB>().cartDao() }
    single{
        val passphrase: ByteArray = SQLiteDatabase.getBytes("indomiesaltedeggs".toCharArray())
        val factory = SupportFactory(passphrase)
        Room.databaseBuilder(
            androidContext(),
            CartDB::class.java,
            "cart.db"
        )
            .fallbackToDestructiveMigration()
            .openHelperFactory(factory)
            .build()
    }
}

val viewModelMod = module{
    viewModel{CartViewModel(get())}
}