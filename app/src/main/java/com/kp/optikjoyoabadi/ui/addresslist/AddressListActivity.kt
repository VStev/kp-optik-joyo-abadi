package com.kp.optikjoyoabadi.ui.addresslist

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.toObject
import com.kp.optikjoyoabadi.R
import com.kp.optikjoyoabadi.adapters.AddressAdapter
import com.kp.optikjoyoabadi.databinding.ActivityAddressListBinding
import com.kp.optikjoyoabadi.getFirebaseFirestoreInstance
import com.kp.optikjoyoabadi.model.Address

class AddressListActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var addressAdapter: AddressAdapter
    private lateinit var binding: ActivityAddressListBinding
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
        setContentView(binding.root)
        showLayout()
    }

    private fun showLayout() {
        //remove after development
        FirebaseFirestore.setLoggingEnabled(true)
        val user = auth.currentUser
        val rv: RecyclerView = findViewById(R.id.recycler_address)
        val query = user?.let {
            fireDB.collection("Address")
                .whereArrayContains("consumerId", it.uid)
        }
        //creates new anonymous object that returns the adapter
        addressAdapter = object : AddressAdapter(query) {
            override fun onDataChanged() {
                super.onDataChanged()
                if (itemCount == 0){
                    binding.recyclerAddress.visibility = View.GONE
//                    binding.noItemLayout.visibility = View.VISIBLE
                }else{
                    binding.recyclerAddress.visibility = View.VISIBLE
//                    binding.noItemLayout.visibility = View.GONE
                }
            }

            override fun onError(e: FirebaseFirestoreException) {
                Snackbar.make(binding.root, "Error connecting to database", Snackbar.LENGTH_LONG).show()
            }
        }
        addressAdapter.setOnItemClickCallback(object:AddressAdapter.OnItemClickCallback{
            override fun onItemClicked(addressId: String?) {
//                val notMain = user?.let {
//                    fireDB.collection("Address")
//                        .whereArrayContains("consumerId", it.uid)
//                        .whereEqualTo("isMain", false)
//                }
//                val main = user?.let {
//                    fireDB.collection("Address")
//                        .whereArrayContains("consumerId", it.uid)
//                        .whereEqualTo("isMain", false)
//                }
                val result = query?.get()
                if (result != null) {
                    result.result?.forEach {
                        val address = it.toObject<Address>()
                        if (address.addressId != addressId){
                            fireDB.collection("Address").document(address.addressId)
                                .update(
                                    mapOf(
                                        "isMain" to false
                                    )
                                )
                        }else{
                            fireDB.collection("Address").document(address.addressId)
                                .update(
                                    mapOf(
                                        "isMain" to true
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
}