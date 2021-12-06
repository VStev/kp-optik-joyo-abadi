package com.kp.optikjoyoabadi.ui.addresslist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kp.optikjoyoabadi.R
import com.kp.optikjoyoabadi.adapters.AddressAdapter
import com.kp.optikjoyoabadi.databinding.FragmentAddressListBinding

class AddressListFragment : Fragment() {

    private lateinit var addressAdapter: AddressAdapter
    private val fireDB = Firebase.firestore
    private var _binding: FragmentAddressListBinding? = null
    private val binding get() = _binding!!

    override fun onStart() {
        super.onStart()
        addressAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        addressAdapter.stopListening()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddressListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //remove after development
        FirebaseFirestore.setLoggingEnabled(true)
        val rv: RecyclerView = view.findViewById(R.id.recycler_address)
        val query = fireDB.collection("Transactions")
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
        with (rv){
            layoutManager = LinearLayoutManager(context)
            adapter = addressAdapter
        }
    }
}