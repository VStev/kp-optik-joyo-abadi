package com.kp.optikjoyoabadi.di

import androidx.room.Room
import com.kp.optikjoyoabadi.dataobjects.room.CartDB
import com.kp.optikjoyoabadi.dataobjects.room.CartRepository
import com.kp.optikjoyoabadi.ui.addaddress.AddAddressViewModel
import com.kp.optikjoyoabadi.ui.cart.CartViewModel
import com.kp.optikjoyoabadi.ui.checkout.CheckoutViewModel
import com.kp.optikjoyoabadi.ui.paymentdetail.PaymentDetailViewModel
import com.kp.optikjoyoabadi.ui.productdetail.ProductDetailViewModel
import com.kp.optikjoyoabadi.ui.profile.ProfileViewModel
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

val repoModule = module{
    single{ CartRepository(get()) }
}

val viewModelMod = module{
    viewModel{ CartViewModel(get()) }
    viewModel { AddAddressViewModel() }
    viewModel { PaymentDetailViewModel() }
    viewModel { CheckoutViewModel(get()) }
    viewModel { ProductDetailViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
}