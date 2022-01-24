package com.kp.optikjoyoabadi.ui.paymentdetail

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.kp.optikjoyoabadi.R
import com.kp.optikjoyoabadi.databinding.ActivityPaymentDetailBinding
import com.kp.optikjoyoabadi.getFirebaseFirestoreInstance
import com.kp.optikjoyoabadi.model.Payment
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.properties.Delegates

class PaymentDetailActivity : AppCompatActivity() {

    private val fireDB = getFirebaseFirestoreInstance()
    private val paymentViewModel: PaymentDetailViewModel by viewModel()
    private lateinit var binding: ActivityPaymentDetailBinding
    private lateinit var transactionID: String
    private lateinit var imageUri: Uri
    private var payAmt by Delegates.notNull<Int>()

    companion object {
        const val  EXTRA_PAYID = "payment1"
        const val EXTRA_PAYMENT = "123"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        transactionID = intent.getStringExtra(EXTRA_PAYID).toString()
        payAmt = intent.getIntExtra(EXTRA_PAYMENT, 0)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = ""
        showLayout()
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding.buttonUpload.setOnClickListener {
            when{
                (imageUri.toString().isEmpty() || imageUri.toString().isBlank()) -> {
                    Toast.makeText(this, "Bukti bayar belum dipilih, silahkan klik pilih bukti bayar", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    paymentViewModel.submitData(transactionID, imageUri).observe(this, {
                        when{
                            (it == "Success!") -> showLayout()
                            else -> Toast.makeText(this, "UPLOAD FAILED", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }
        }

        binding.buttonPilihGambar.setOnClickListener {
            val options = arrayOf<CharSequence>("Choose from Gallery", "Cancel")
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Choose product picture")
            builder.setItems(options) { dialog, item ->
                when {
                    options[item] == "Choose from Gallery" -> {
                        val pickPhoto =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        resultLauncher.launch(pickPhoto)
                    }
                    options[item] == "Cancel" -> {
                        dialog.dismiss()
                    }
                }
            }
            builder.show()
        }
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                val selectedImage: Uri? = it.data!!.data
                if (selectedImage != null) {
                    imageUri = selectedImage
                    binding.buktiBayar.setImageURI(imageUri)
                }
            }
        }

    private fun showLayout() {
        //remove the line of code below after done developing
        FirebaseFirestore.setLoggingEnabled(true)
        //remove the line of code above after done developing
        val query = fireDB.collection("Payment").document(transactionID)
        val reference = Firebase.storage.reference
        query.get()
            .addOnCompleteListener {
                val paymentDetail = it.result?.toObject<Payment>()
                if (paymentDetail?.proof != null) {
                    if (paymentDetail.transactionId != transactionID){
                        Toast.makeText(baseContext, "TERJADI KESALAHAN DALAM MEMUAT DATA", Toast.LENGTH_SHORT).show()
                    }else{
                        binding.invoiceNumber.text = paymentDetail.transactionId
                        binding.paymentDate.text = paymentDetail.receivedAt.toString()
                        binding.totalBayar.text = paymentDetail.amount.toString()
                        binding.buttonUpload.visibility = View.GONE
                        binding.buttonPilihGambar.visibility = View.GONE
                        val image = reference.child("Payment/${paymentDetail.proof}")
                        Glide.with(binding.root)
                            .load(image)
                            .override(256,256)
                            .into(binding.buktiBayar)
                    }
                }
            }
            .addOnFailureListener {
                binding.invoiceNumber.text = transactionID
                binding.paymentDate.text = getString(R.string.belum_bayar)
                binding.totalBayar.text = payAmt.toString()
            }
    }
}