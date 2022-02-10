package com.kp.optikjoyoabadi.ui.checkout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.kp.optikjoyoabadi.MainActivity
import com.kp.optikjoyoabadi.R
import com.kp.optikjoyoabadi.adapters.ChangeAddressAdapter
import com.kp.optikjoyoabadi.adapters.CheckoutAdapter
import com.kp.optikjoyoabadi.databinding.ActivityCheckoutBinding
import com.kp.optikjoyoabadi.getFirebaseFirestoreInstance
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.kp.optikjoyoabadi.model.Address
import com.kp.optikjoyoabadi.model.Cart
import com.kp.optikjoyoabadi.model.Product
import com.kp.optikjoyoabadi.ui.paymentmethod.PaymentMethodActivity
import kotlin.math.ceil
import kotlin.properties.Delegates

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private var address = Address()
    private var addressList = ArrayList<Address>()
    private var itemList = ArrayList<Cart>()
    private lateinit var cardAdapter: CheckoutAdapter
    private var isBuyNow = false
    private var product: Cart? = null
    private var subTotal = 0
    private var quantity = 0
    private var shipping = 0
    private val fireDb = getFirebaseFirestoreInstance()
    private val auth = Firebase.auth.currentUser
    private val checkoutViewModel: CheckoutViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        isBuyNow = intent.getBooleanExtra(EXTRA_BUYNOW, false)
        product = intent.getParcelableExtra<Cart>(EXTRA_CART)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setLayout()
        setListeners()
    }

    private fun setListeners() {
        binding.buttonCheckout.setOnClickListener {
            if (auth != null) {
                val headerTitle = product?.productType ?: "${itemList[0].productType} dan lainnya"
                itemList.forEach {
                    fireDb.collection("Products").document(it.productId)
                        .get().addOnSuccessListener { product ->
                            val prod = product.toObject<Product>()
                            val newstock = prod?.stock?.minus(it.quantity)
                            fireDb.collection("Products").document(it.productId).update("stock", newstock)
                        }
                }
                checkoutViewModel.insertToTransactionAndDetail(itemList, address, shipping, subTotal, auth.uid, headerTitle, isBuyNow).observe(this) {
                    when {
                        (it == "success") -> {
                            startActivity(Intent(this, PaymentMethodActivity::class.java))
                            Toast.makeText(
                                this,
                                "Transaksi sukses, cek daftar transaksi",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                        (it == "onProcess") -> {

                        }
                        else -> Toast.makeText(this, "Transaksi gagal! $it", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

        binding.ubahAlamatKirim.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.change_address_alert_dialog_box, null)
            builder.setView(view)
            val dialog = builder.create()
            val recycler = view.findViewById<RecyclerView>(R.id.rv_change_address)
            val rvAdapter = ChangeAddressAdapter()
            rvAdapter.setData(addressList)
            rvAdapter.setOnItemClickCallback(object: ChangeAddressAdapter.OnItemClickCallback{
                override fun onItemClicked(position: Int) {
                    address = addressList[position]
                    displayAddress(address)
                    updateShippingFeeAndTotal(subTotal, address.city, address.region, quantity)
                    dialog.dismiss()
                }
            })
            with(recycler){
                adapter = rvAdapter
                layoutManager = LinearLayoutManager(context)
            }
            dialog.show()
        }
    }

    private fun setLayout() {
        FirebaseFirestore.setLoggingEnabled(true)
        val addressQuery = auth?.let {
            fireDb.collection("Address")
                .whereEqualTo("UID", it.uid)
        }
        addressQuery?.get()?.addOnSuccessListener {
            it.documents.forEach { documentData ->
                val data = documentData.toObject<Address>()
                if (data != null) {
                    addressList.add(data)
                    if (data.main){
                        address = data
                        updateShippingFeeAndTotal(subTotal, address.city, address.region, quantity)
                        displayAddress(address)
                    }
                }
            }
        }?.addOnFailureListener {

        }
        val rv = binding.recyclerCheckout
        cardAdapter = CheckoutAdapter()
        if (isBuyNow){
            if (product != null){
                itemList.add(product!!)
                cardAdapter.setData(itemList)
                quantity = product!!.quantity
                subTotal = product!!.price * product!!.quantity
                val subText = "Rp. $subTotal"
                binding.txtSubtotal.text = subText
            }
        }else{
            checkoutViewModel.cartItems().observe(this) { Cart ->
                cardAdapter.setData(Cart)
                subTotal = 0
                Cart.forEach {
                    itemList.add(it)
                    subTotal += (it.price * it.quantity)
                    quantity += it.quantity
                }
                val subText = "Rp. $subTotal"
                binding.txtSubtotal.text = subText
            }
        }
        with (rv){
            layoutManager = LinearLayoutManager(context)
            adapter = cardAdapter
        }
    }

    private fun displayAddress(address: Address) {
        val reg = "${address.city}, ${address.region}"
        binding.namaPenerima.text = address.recipientName
        binding.nomorTeleponPenerima.text = address.phoneNumber
        binding.alamatPenerima.text = address.street
        binding.regionPenerima.text = reg
        binding.postalCodePenerima.text = address.postalCode.toString()
    }

    private fun updateShippingFeeAndTotal(subTotal: Int, city: String, region: String, qty: Int) {
        shipping = if (city == "Surabaya" && region == "Jawa Timur"){
            10000 * ceil(qty/4.0).toInt()
        }else if (region.contains("Jawa")){
            15000 * ceil(qty/4.0).toInt()
        }else{
            25000 * ceil(qty/4.0).toInt()
        }
        val shipText = "Rp. $shipping"
        binding.txtShipping.text = shipText
        val total = subTotal + shipping
        val totalText = "Rp. $total"
        binding.txtTotal.text = totalText
    }

    companion object {
        const val EXTRA_CART = "b"
        const val EXTRA_BUYNOW = "buynow"
    }
}