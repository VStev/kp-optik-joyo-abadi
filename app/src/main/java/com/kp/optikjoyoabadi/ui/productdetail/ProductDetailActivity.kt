package com.kp.optikjoyoabadi.ui.productdetail

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.actionCodeSettings
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.kp.optikjoyoabadi.GlideApp
import com.kp.optikjoyoabadi.R
import com.kp.optikjoyoabadi.databinding.ActivityProductDetailBinding
import com.kp.optikjoyoabadi.getFirebaseFirestoreInstance
import com.kp.optikjoyoabadi.model.Cart
import com.kp.optikjoyoabadi.model.Product
import com.kp.optikjoyoabadi.ui.checkout.CheckoutActivity
import com.kp.optikjoyoabadi.ui.detailtutor.TutorialRecipeActivity
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
                    val view = layoutInflater.inflate(R.layout.set_qty_note_alert_dialog_box, null)
                    builder.setView(view)
                    builder.setPositiveButton(getString(R.string.tambah_ke_keranjang)) { _, _ ->
                        val lensSel = view.findViewById<Spinner>(R.id.addLensa).selectedItemPosition
                        val lens = if (lensSel == 1) "Anti Radiasi" else "Normal Plastik"
                        val addPrice = if (lensSel == 1) 35000 else 0
                        val sphereL = view.findViewById<EditText>(R.id.addSphereL).text.toString()
                        val sphereR = view.findViewById<EditText>(R.id.addSphereR).text.toString()
                        val silinderL = view.findViewById<EditText>(R.id.addSilinderL).text.toString()
                        val silinderR = view.findViewById<EditText>(R.id.addSilinderR).text.toString()
                        val axisL = view.findViewById<EditText>(R.id.addAxisL).text.toString()
                        val axisR = view.findViewById<EditText>(R.id.addAxisR).text.toString()
                        val pd = view.findViewById<EditText>(R.id.addPupilaryDistance).text.toString()
                        val note = "Lensa $lens | Sphere $sphereL / $sphereR | Cyl $silinderL / $silinderR " +
                                "| Axis $axisL / $axisR | PD $pd"
                        val qty = view.findViewById<EditText>(R.id.addQty).text.toString()
                        val quantity = if (qty.isEmpty()) 1 else qty.toInt()
                        if (quantity > product.stock){
                            Toast.makeText(baseContext, "Jumlah melebihi stok", Toast.LENGTH_LONG).show()
                        }else{
                            productViewModel.addToCart(product, note, quantity, addPrice)
                        }
                    }
                    builder.setNegativeButton(getString(R.string.batal)){dialog, _ ->
                        dialog.cancel()
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

        binding.buttonBeliSekarang.setOnClickListener {
            val user = auth.currentUser
            if (user != null) {
                if (user.isEmailVerified) {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                    val view = layoutInflater.inflate(R.layout.set_qty_note_alert_dialog_box, null)
                    builder.setView(view)
                    builder.setPositiveButton(getString(R.string.belisekarang)) { _, _ ->
                        val lensSel = view.findViewById<Spinner>(R.id.addLensa).selectedItemPosition
                        val lens = if (lensSel == 1) "Anti Radiasi" else "Normal Plastik"
                        val addPrice = if (lensSel == 1) 35000 else 0
                        val sphereL = view.findViewById<EditText>(R.id.addSphereL).text.toString()
                        val sphereR = view.findViewById<EditText>(R.id.addSphereR).text.toString()
                        val silinderL = view.findViewById<EditText>(R.id.addSilinderL).text.toString()
                        val silinderR = view.findViewById<EditText>(R.id.addSilinderR).text.toString()
                        val axisL = view.findViewById<EditText>(R.id.addAxisL).text.toString()
                        val axisR = view.findViewById<EditText>(R.id.addAxisR).text.toString()
                        val pd = view.findViewById<EditText>(R.id.addPupilaryDistance).text.toString()
                        val note = "Lensa $lens | Sphere $sphereL / $sphereR | Cyl $silinderL / $silinderR " +
                                "| Axis $axisL / $axisR | PD $pd"
                        val qty = view.findViewById<EditText>(R.id.addQty).text.toString()
                        val quantity = if (qty.isEmpty()) 1 else qty.toInt()
                        if (quantity > product.stock){
                            Toast.makeText(baseContext, "Jumlah melebihi stok", Toast.LENGTH_LONG).show()
                        }else{
                            val cart = Cart(
                                product.productId,
                                product.productName,
                                product.category,
                                note,
                                product.price + addPrice,
                                quantity,
                                product.image_url
                            )
                            val intent = Intent(this, CheckoutActivity::class.java)
                            intent.putExtra(CheckoutActivity.EXTRA_BUYNOW, true)
                            intent.putExtra(CheckoutActivity.EXTRA_CART, cart)
                            startActivity(intent)
                        }
                    }
                    builder.setNegativeButton(getString(R.string.batal)){dialog, _ ->
                        dialog.cancel()
                    }
                    builder.show()
                } else {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                    builder.setTitle(getString(R.string.not_yet_verified))
                    builder.setMessage(getString(R.string.not_yet_verified_message))
                    builder.setPositiveButton(getString(R.string.kirim_ulang)) { dialog, _ ->
                        sendEmail(user)
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

        binding.btnPanduanResep.setOnClickListener {
            startActivity(Intent(this, TutorialRecipeActivity::class.java))
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
                binding.txtPrice.text = "Rp. ${product.price}"
                binding.txtProductDetail.text = product.details
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun sendEmail(user: FirebaseUser) {
        val actionCodeSettings = actionCodeSettings {
            url = "https://optik-joyo-abadi.firebaseapp.com/?email=${user.email}"
            handleCodeInApp = true
            setAndroidPackageName(
                "com.kp.optikjoyoabadi",
                true,
                null
            )
        }
        user.email?.let { it1 -> auth.sendSignInLinkToEmail(it1, actionCodeSettings) }
    }

}