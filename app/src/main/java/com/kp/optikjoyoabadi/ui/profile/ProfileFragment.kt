package com.kp.optikjoyoabadi.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kp.optikjoyoabadi.databinding.FragmentProfileBinding
import com.kp.optikjoyoabadi.ui.addresslist.AddressListActivity
import com.kp.optikjoyoabadi.ui.loginsignup.LoginActivity
import com.kp.optikjoyoabadi.ui.transactionlist.TransactionListActivity

class ProfileFragment : Fragment() {

    private val auth = Firebase.auth
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = auth.currentUser
        if (user != null) {
            binding.txtDisplayName.text = user.displayName
            binding.txtEmail.text = user.email
            binding.txtVerificationStatus.text = if (user.isEmailVerified){
                "Verified"
            }else{
                "Unverified"
            }
            setonClickListeners()
        }else{
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setonClickListeners() {
        binding.cardAddressList.setOnClickListener {
            val intent = Intent(context, AddressListActivity::class.java)
            startActivity(intent)
        }

        binding.cardTransactions.setOnClickListener {
            val intent = Intent(context, TransactionListActivity::class.java)
            startActivity(intent)
        }

        binding.cardSecurity.setOnClickListener {

        }
    }
}