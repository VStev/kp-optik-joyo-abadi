package com.kp.optikjoyoabadi.ui.productdetail

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
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
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = ""
        productId = intent.getStringExtra(EXTRA_ID).toString()
        showLayout()
        setListeners()
    }

    private fun setListeners() {
        binding.buttonAtc.setOnClickListener {
            val user = auth.currentUser
            if (user != null) {
                if (user.isEmailVerified) {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                    builder.setView(R.layout.set_qty_note_alert_dialog_box)
                    builder.setPositiveButton(getString(R.string.tambah_ke_keranjang)) { _, _ ->
                        val note = findViewById<EditText>(R.id.addNote).text.toString()
                        val qty = findViewById<EditText>(R.id.addQty).text.toString().toInt()
                        productViewModel.addToCart(product, note, qty)
                    }
                    builder.setNegativeButton(getString(R.string.batal)){dialog, _ ->
                        dialog.dismiss()
                    }
                    builder.show()
                } else {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                    builder.setTitle(getString(R.string.not_yet_verified))
                    builder.setMessage(getString(R.string.not_yet_verified_message))
                    builder.setPositiveButton(getString(R.string.kirim_ulang)) { dialog, _ ->
                        user.sendEmailVerification()
                        dialog.dismiss()
                    }
                    builder.setNegativeButton(getString(R.string.tutup)){ dialog, _ ->
                        dialog.dismiss()
                    }
                    builder.show()
                }
            } else {
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
//                    .fitCenter()
                    .centerCrop()
                    .into(binding.productImage)
                binding.txtProductName.text = product.productName
                if (product.stock > 0) {
                    binding.txtStock.text = getString(R.string.stok_tersedia)
                } else {
                    binding.txtStock.text = getString(R.string.stok_habis)
                    binding.buttonAtc.isEnabled = false
                }
                binding.txtCategory.text = product.category
                binding.txtPrice.text = product.price.toString()
                binding.txtProductDetail.text = product.details
            }
    }


}