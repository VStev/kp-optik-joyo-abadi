package com.kp.optikjoyoabadi.ui.transactiondetail

import android.R.attr.label
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.kp.optikjoyoabadi.adapters.TransactionDetailAdapter
import com.kp.optikjoyoabadi.databinding.ActivityTransactionDetailBinding
import com.kp.optikjoyoabadi.getFirebaseFirestoreInstance
import com.kp.optikjoyoabadi.model.Transaction
import com.kp.optikjoyoabadi.ui.paymentdetail.PaymentDetailActivity
import com.kp.optikjoyoabadi.ui.paymentmethod.PaymentMethodActivity
import java.util.*


class TransactionDetailActivity : AppCompatActivity() {
    private lateinit var detailAdapter: TransactionDetailAdapter
    private var _binding: ActivityTransactionDetailBinding? = null
    private val fireDB = getFirebaseFirestoreInstance()
    private lateinit var noResi: String
    private lateinit var transactionId: String
    private val binding get() = _binding!!

    companion object {
        const val EXTRA_ID = "DEMOTRANS"
    }

    override fun onStart() {
        super.onStart()
        detailAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        detailAdapter.stopListening()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTransactionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        transactionId = intent.getStringExtra(EXTRA_ID).toString()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = ""
        showLayout()
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        val reference = fireDB.collection("Transactions").document(transactionId)

        binding.buttonCancelOrder.setOnClickListener {
            reference
                .update("status", "CANCELLED")
                .addOnSuccessListener {
                    Toast.makeText(baseContext, "Pesanan telah dibatalkan.",
                        Toast.LENGTH_SHORT).show()
                    binding.statusTransaksi.text = "CANCELLED"
                    binding.buttonCancelOrder.visibility = View.GONE
                }
                .addOnFailureListener {
                    Toast.makeText(baseContext, "Gagal mengubah status pesanan! $it",
                        Toast.LENGTH_SHORT).show()
                }
        }

        binding.buttonFinishOrder.setOnClickListener {
            reference
                .update("status", "FINISHED")
                .addOnSuccessListener {
                    Toast.makeText(baseContext, "Pesanan telah selesai.",
                        Toast.LENGTH_SHORT).show()
                    binding.statusTransaksi.text = "FINISHED"
                    binding.buttonFinishOrder.visibility = View.GONE
                }
                .addOnFailureListener {
                    Toast.makeText(baseContext, "Gagal mengubah status pesanan! $it",
                        Toast.LENGTH_SHORT).show()
                }
        }

        binding.buttonHowToPay.setOnClickListener {
            startActivity(Intent(this, PaymentMethodActivity::class.java))
        }

        binding.buttonCheckReceipt.setOnClickListener {
            val intent = Intent(it.context, PaymentDetailActivity::class.java)
            intent.putExtra(PaymentDetailActivity.EXTRA_PAYID, transactionId)
            it.context.startActivity(intent)
            finish()
        }

        binding.btnCopyResi.setOnClickListener {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("nomor resi", noResi)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Nomor resi telah disalin", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLayout() {
        //remove the line of code below after done developing
        FirebaseFirestore.setLoggingEnabled(true)
        //remove the line of code above after done developing
        val rv: RecyclerView = binding.recyclerTransactionItem
        val query = fireDB.collection("Transactions").document(transactionId)
        val rvQuery = fireDB.collection("TransactionDetail")
            .whereEqualTo("transactionId", transactionId)
        val reference = Firebase.storage.reference
        query.get().addOnCompleteListener {
            if (it.isSuccessful){
                val data = it.result?.toObject<Transaction>()
                val buyDate = data?.dateTime?.toDate()
                val cal = Calendar.getInstance()
                if (buyDate != null){
                    cal.time = buyDate
                }
                cal.add(Calendar.DATE, 3)
                val expiryDate = cal.time
                val region = data?.city + data?.region
                binding.transactionInvoices.text = data?.transactionId
                binding.tanggalBeli.text = data?.dateTime?.toDate().toString()
                binding.statusTransaksi.text = data?.status
                binding.namaPenerima.text = data?.recipientName
                binding.nomorTeleponPenerima.text = data?.phoneNumber
                binding.alamatPenerima.text = data?.street
                binding.regionPenerima.text = region
                binding.postalCodePenerima.text = data?.postalCode.toString()
                binding.subtotalPrice.text = "Rp. ${data?.subTotal.toString()}"
                binding.shippingPrice.text = "Rp. ${data?.shippingFee.toString()}"
                binding.totalPrice.text = "Rp. ${data?.total.toString()}"
                if (data?.shippingNumber != ""){
                    if (data != null) {
                        noResi = data.shippingNumber.toString()
                    }
                    binding.noResiPenerima.text = "Nomor Resi: ${data?.shippingNumber}"
                    binding.noResiPenerima.visibility = View.VISIBLE
                    binding.btnCopyResi.visibility = View.VISIBLE
                }
                if (data != null) {
                    if (data.status == "WAITING FOR PAYMENT"){
                        binding.textBatasBayar.text = "Tanggal batas pembayaran $expiryDate"
                    }else{
                        binding.textBatasBayar.visibility = View.GONE
                    }
                    if (data.status == "UNCONFIRMED" || data.status == "WAITING FOR PAYMENT" || data.status != "CANCELLED"
                        || data.status != "FINISHED"){
                        binding.buttonCancelOrder.visibility = View.VISIBLE
                    }
                    if (data.status == "CANCELLED"){
                        binding.buttonFinishOrder.visibility = View.GONE
                        binding.buttonCancelOrder.visibility = View.GONE
                    }
                    if (data.status == "SHIPPED"){
                        binding.buttonFinishOrder.visibility = View.VISIBLE
                        binding.buttonCancelOrder.visibility = View.GONE
                    }else{
                        binding.buttonFinishOrder.visibility = View.GONE
                    }
                    if (data.status == "FINISHED"){
                        binding.buttonFinishOrder.visibility = View.GONE
                        binding.buttonCancelOrder.visibility = View.GONE
                    }
                }
            }else{
                Log.w("TAG", "loadDetail:failure", it.exception)
                Toast.makeText(baseContext, "Failed to load transaction detail",
                    Toast.LENGTH_SHORT).show()
            }
        }
        detailAdapter = object : TransactionDetailAdapter(rvQuery, reference){
            override fun onDataChanged() {
                super.onDataChanged()
                return
            }
            override fun onError(e: FirebaseFirestoreException) {
                Snackbar.make(binding.root, "Error connecting to database", Snackbar.LENGTH_LONG).show()
            }
        }
        with (rv){
            layoutManager = LinearLayoutManager(context)
            adapter = detailAdapter
        }
    }

}