package com.kp.optikjoyoabadi.ui.paymentdetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.kp.optikjoyoabadi.R
import com.kp.optikjoyoabadi.databinding.ActivityPaymentDetailBinding
import com.kp.optikjoyoabadi.model.Payment
import kotlin.properties.Delegates

class PaymentDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentDetailBinding
    private val fireDB = Firebase.firestore
    private lateinit var transactionID: String
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
        showLayout()
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        TODO("Not yet implemented")
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
                if (paymentDetail != null) {
                    if (paymentDetail.transactionId != transactionID){
                        Toast.makeText(baseContext, "TERJADI KESALAHAN DALAM MEMUAT DATA", Toast.LENGTH_SHORT).show()
                    }else{
                        binding.invoiceNumber.text = paymentDetail.transactionId
                        binding.paymentDate.text = paymentDetail.receivedAt.toString()
                        binding.totalBayar.text = paymentDetail.amount.toString()
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