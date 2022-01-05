package com.kp.optikjoyoabadi.ui.productdetail

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.kp.optikjoyoabadi.GlideApp
import com.kp.optikjoyoabadi.R
import com.kp.optikjoyoabadi.databinding.ActivityProductDetailBinding
import com.kp.optikjoyoabadi.getFirebaseFirestoreInstance
import com.kp.optikjoyoabadi.model.Product
import com.kp.optikjoyoabadi.ui.loginsignup.LoginActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private val fireDb = getFirebaseFirestoreInstance()
    private val auth = Firebase.auth
    private val reference = Firebase.storage.reference
    private lateinit var productId: String
    private lateinit var product: Product
    private val productViewModel: ProductDetailViewModel by viewModel()

    companion object {
        const val EXTRA_ID = "id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        productId = intent.getStringExtra(EXTRA_ID).toString()
        showLayout()
        setListeners()
    }

    private fun setListeners() {
        binding.buttonAtc.setOnClickListener {
            val user = auth.currentUser
            if (user != null) {
                if (user.isEmailVerified){
                    //TODO(popup add note and qty)
                    productViewModel.addToCart(product, "a", 1)
                }else{
                    //TODO(alertdialog not yet verified)
                }
            }else{
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun showLayout() {
        val query = fireDb.collection("Products").document(productId)
        query.get()
            .addOnSuccessListener {
                product = it.toObject<Product>()!!
                val image = product.let { it1 -> reference.child("products/${it1.image_url}") }
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