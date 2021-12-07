package com.kp.optikjoyoabadi.ui.productlist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.kp.optikjoyoabadi.R
import com.kp.optikjoyoabadi.adapters.ProductAdapter
import com.kp.optikjoyoabadi.databinding.ActivityProductListBinding

//use setQuery from FirestoreAdapter to handle filters

class ProductListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductListBinding
    private val fireDb = Firebase.firestore
    private lateinit var productAdapter: ProductAdapter
    private lateinit var argument: String

    companion object {
        const val EXTRA_ARGUMENT = "kacamata"
    }

    override fun onStart() {
        super.onStart()
        productAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        productAdapter.stopListening()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        argument = intent.getStringExtra(EXTRA_ARGUMENT).toString()
        binding = ActivityProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showLayout()
    }

    private fun showLayout() {
        //remove the line of code below after done developing
        FirebaseFirestore.setLoggingEnabled(true)
        //remove the line of code above after done developing
        val rv: RecyclerView = findViewById(R.id.recycler_product)
        val query = fireDb.collection("Products")
            .whereEqualTo("Category", argument)
        val reference = Firebase.storage.reference
        productAdapter = object : ProductAdapter(query, reference) {
            override fun onDataChanged() {
                super.onDataChanged()
                if (itemCount == 0){
                    binding.recyclerProduct.visibility = View.GONE
//                    binding.noItemLayout.visibility = View.VISIBLE
                }else{
                    binding.recyclerProduct.visibility = View.VISIBLE
//                    binding.noItemLayout.visibility = View.GONE
                }
            }

            override fun onError(e: FirebaseFirestoreException) {
                Snackbar.make(binding.root, "Error connecting to database", Snackbar.LENGTH_LONG).show()
            }
        }
        with (rv){
            layoutManager = LinearLayoutManager(context)
            adapter = productAdapter
        }
    }
}