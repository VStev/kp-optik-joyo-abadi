package com.kp.optikjoyoabadi.ui.addresslist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.kp.optikjoyoabadi.R
import com.kp.optikjoyoabadi.adapters.AddressAdapter
import com.kp.optikjoyoabadi.databinding.ActivityAddressListBinding
import com.kp.optikjoyoabadi.getFirebaseFirestoreInstance
import com.kp.optikjoyoabadi.model.Address
import com.kp.optikjoyoabadi.ui.addaddress.AddAddressActivity
import kotlin.properties.Delegates

class AddressListActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var addressAdapter: AddressAdapter
    private lateinit var binding: ActivityAddressListBinding
    private var flag = false
    private val fireDB = getFirebaseFirestoreInstance()

    override fun onStart() {
        super.onStart()
        addressAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        addressAdapter.stopListening()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressListBinding.inflate(layoutInflater)
        title = "Daftar Alamat"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(binding.root)
        showLayout()
    }

    private fun showLayout() {
        auth = Firebase.auth
        val user = auth.currentUser
        val rv: RecyclerView = findViewById(R.id.recycler_address)
        val query = user?.let {
            fireDB.collection("Address")
                .whereEqualTo("UID", it.uid)
        }
        //creates new anonymous object that returns the adapter
        addressAdapter = object : AddressAdapter(query) {
            override fun onDataChanged() {
                super.onDataChanged()
                if (itemCount == 0){
                    flag = true
                    binding.recyclerAddress.visibility = View.GONE
                    binding.noItemLayout.visibility = View.VISIBLE
                }else{
                    binding.recyclerAddress.visibility = View.VISIBLE
                    binding.noItemLayout.visibility = View.GONE
                }
            }

            override fun onError(e: FirebaseFirestoreException) {
                Snackbar.make(binding.root, "Error connecting to database", Snackbar.LENGTH_LONG).show()
            }
        }
        addressAdapter.setOnItemClickCallback(object:AddressAdapter.OnItemClickCallback{
            override fun onItemClicked(addressId: String?) {
                query?.get()?.addOnSuccessListener { snapshot ->
                    snapshot?.forEach {
                        val address = it.toObject<Address>()
                        if (address.addressId != addressId){
                            fireDB.collection("Address").document(address.addressId)
                                .update(
                                    mapOf(
                                        "main" to false
                                    )
                                )
                        }else{
                            fireDB.collection("Address").document(address.addressId)
                                .update(
                                    mapOf(
                                        "main" to true
                                    )
                                )
                        }
                    }
                }
            }
        })
        with (rv){
            layoutManager = LinearLayoutManager(context)
            adapter = addressAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_address_list, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_address){
            val intent = Intent(this, AddAddressActivity::class.java)
            if (flag){
                intent.putExtra(AddAddressActivity.EXTRA_PARAM, "first")
            }else{
                intent.putExtra(AddAddressActivity.EXTRA_PARAM, "new")
            }
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

}