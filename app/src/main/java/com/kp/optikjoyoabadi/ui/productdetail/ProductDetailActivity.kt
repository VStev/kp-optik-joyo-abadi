package com.kp.optikjoyoabadi.ui.productdetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.NonNull
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.kp.optikjoyoabadi.GlideApp
import com.kp.optikjoyoabadi.R
import com.kp.optikjoyoabadi.databinding.ActivityProductDetailBinding
import com.kp.optikjoyoabadi.model.Product

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private val fireDb = Firebase.firestore
    private val auth = Firebase.auth
    private val reference = Firebase.storage.reference
    private lateinit var productId: String

    companion object {
        const val EXTRA_ID = "id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        productId = intent.getStringExtra(EXTRA_ID).toString()
        setLayout()
        setListeners()
    }

    private fun setListeners() {
        binding.buttonAtc.setOnClickListener {
            val user = auth.currentUser
            if (user != null) {
                if (user.isEmailVerified){
                    //add to cart
                }else{
                    //alertdialog
                }
            }
        }
    }

    private fun setLayout() {
        val query = fireDb.collection("Products").document(productId)
        query.get()
            .addOnSuccessListener {
                val product = it.toObject<Product>()
                if (product != null){
                    val image = product.let { it1 -> reference.child(it1.image_url) }
                    GlideApp.with(binding.root)
                        .load(image)
                        .into(binding.productImage)
                    binding.txtProductName.text = product.productName
                    binding.txtStock.text = if (product.stock > 0){
                        getString(R.string.stok_tersedia)
                    }else{
                        getString(R.string.stok_habis)
                    }
                    binding.txtCategory.text = product.category
                    binding.txtPrice.text = product.price.toString()
                    binding.txtProductDetail.text = product.details
                }
            }
    }
}