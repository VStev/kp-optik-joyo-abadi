package com.kp.optikjoyoabadi.ui.productlist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.kp.optikjoyoabadi.R
import com.kp.optikjoyoabadi.adapters.ProductAdapter
import com.kp.optikjoyoabadi.databinding.ActivityProductListBinding
import com.kp.optikjoyoabadi.getFirebaseFirestoreInstance

//use setQuery from FirestoreAdapter to handle filters

class ProductListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductListBinding
    private val fireDb = getFirebaseFirestoreInstance()
    private lateinit var productAdapter: ProductAdapter
    private lateinit var argument: String
    private var remaining = 0
    private val pageSize = 8

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
        remaining = 0
        //remove the line of code below after done developing
        FirebaseFirestore.setLoggingEnabled(true)
        //remove the line of code above after done developing
        val rv: RecyclerView = findViewById(R.id.recycler_product)
        title = when{
            (argument == "all") -> {
                "Semua produk"
            }
            else -> {
                argument
            }
        }
        val totalQuery = when{
            (argument == "all") -> {
                fireDb.collection("Products")
            }
            else -> {
                fireDb.collection("Products")
                    .whereEqualTo("category", argument)
            }
        }
        totalQuery.get()
            .addOnSuccessListener {
                remaining = it.size()
            }
        val query = when{
            (argument == "all") -> {
                fireDb.collection("Products")
                    .whereEqualTo("deleted", false)
//            .orderBy("productName", Query.Direction.ASCENDING)
                    .limit(8)
            }
            else -> {
                fireDb.collection("Products")
                    .whereEqualTo("category", argument)
                    .whereEqualTo("deleted", false)
//            .orderBy("productName", Query.Direction.ASCENDING)
                    .limit(8)
            }
        }
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
            layoutManager = GridLayoutManager(context, 2)
            adapter = productAdapter
            addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!rv.canScrollVertically(1)){
                        remaining -= pageSize
                        if (remaining > 0){
                            productAdapter.updatePagedQuery(argument)
                        }
                    }
                }
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_product_list, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        argument = when{
            (item.itemId == R.id.glasses_only) -> "Kacamata"
            (item.itemId == R.id.sunglasses_only) -> "Sunglasses"
            else -> "all"
        }
        title = if (argument == "all") "Semua produk" else argument
        productAdapter.updateQuery(argument)
        return super.onOptionsItemSelected(item)
    }
}