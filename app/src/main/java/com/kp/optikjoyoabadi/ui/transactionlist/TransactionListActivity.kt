package com.kp.optikjoyoabadi.ui.transactionlist

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.ktx.Firebase
import com.kp.optikjoyoabadi.R
import com.kp.optikjoyoabadi.adapters.TransactionAdapter
import com.kp.optikjoyoabadi.databinding.ActivityTransactionListBinding
import com.kp.optikjoyoabadi.getFirebaseFirestoreInstance

class TransactionListActivity : AppCompatActivity() {

    private lateinit var transactionAdapter: TransactionAdapter
    private val fireDB = getFirebaseFirestoreInstance()
    private val auth = Firebase.auth
    private lateinit var binding: ActivityTransactionListBinding

    override fun onStart() {
        super.onStart()
        transactionAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        transactionAdapter.stopListening()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        setContentView()
    }

    private fun setContentView() {
        //remove after development
        FirebaseFirestore.setLoggingEnabled(true)
        val rv: RecyclerView = findViewById(R.id.rv_transaction_item)
        val query = auth.currentUser?.let {
            fireDB.collection("Transactions")
                    .whereEqualTo("UID", it.uid)
        }
        transactionAdapter = object : TransactionAdapter(query) {
            override fun onDataChanged() {
                super.onDataChanged()
                if (itemCount == 0){
                    binding.rvTransactionItem.visibility = View.GONE
                    binding.noItemLayout.visibility = View.VISIBLE
                }else{
                    binding.rvTransactionItem.visibility = View.VISIBLE
                    binding.noItemLayout.visibility = View.GONE
                }
            }

            override fun onError(e: FirebaseFirestoreException) {
                Snackbar.make(binding.root, "Error connecting to database", Snackbar.LENGTH_LONG).show()
            }
        }
        with (rv){
            layoutManager = LinearLayoutManager(context)
            adapter = transactionAdapter
        }
    }
}