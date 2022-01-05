package com.kp.optikjoyoabadi.ui.checkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.kp.optikjoyoabadi.adapters.CheckoutAdapter
import com.kp.optikjoyoabadi.databinding.ActivityCheckoutBinding
import com.kp.optikjoyoabadi.getFirebaseFirestoreInstance
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.kp.optikjoyoabadi.model.Address
import com.kp.optikjoyoabadi.model.Cart
import kotlin.properties.Delegates

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var address: Address
    private lateinit var addressList: ArrayList<Address>
    private lateinit var itemList: ArrayList<Cart>
    private lateinit var cardAdapter: CheckoutAdapter
    private var subTotal by Delegates.notNull<Int>()
    private var shipping by Delegates.notNull<Int>()
    private val fireDb = getFirebaseFirestoreInstance()
    private val auth = Firebase.auth.currentUser
    private val checkoutViewModel: CheckoutViewModel by viewModel()

    //TODO(check address, change address, insert to firestore transaction+transactiondetail)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setLayout()
        setListeners()
    }

    private fun setListeners() {
        binding.buttonCheckout.setOnClickListener {
            if (auth != null) {
                checkoutViewModel.insertToTransactionAndDetail(itemList, address, shipping, subTotal, auth.uid)
            }
        }

        binding.ubahAlamatKirim.setOnClickListener {

        }
    }

    private fun setLayout() {
        val addressQuery = auth?.let {
            fireDb.collection("Address")
                .whereArrayContains("consumerId", it.uid)
        }
        addressQuery?.get()?.addOnSuccessListener {
            it.documents.forEach { documentData ->
                val data = documentData.toObject<Address>()
                if (data != null) {
                    addressList.add(data)
                    if (data.isMain){
                        address = data
                    }
                }
            }
        }?.addOnFailureListener {

        }
        cardAdapter = CheckoutAdapter()
        checkoutViewModel.cartItems().observe(this, { Cart ->
            cardAdapter.setData(Cart)
            subTotal = 0
            Cart.forEach{
                itemList.add(it)
                subTotal += (it.price * it.quantity)
            }
            val subText = "Rp. $subTotal"
            binding.txtSubtotal.text = subText
        })
        updateShippingFeeAndTotal(subTotal, address.city, address.region)
    }

    private fun updateShippingFeeAndTotal(subTotal: Int, city: String, region: String) {
        shipping = if (city == "Surabaya" && region == "Jawa Timur"){
            10000
        }else if (region.contains("Jawa")){
            15000
        }else{
            25000
        }
        val shipText = "Rp. $shipping"
        binding.txtShipping.text = shipText
        val total = subTotal + shipping
        val totalText = "Rp. $total"
        binding.txtTotal.text = totalText
    }
}