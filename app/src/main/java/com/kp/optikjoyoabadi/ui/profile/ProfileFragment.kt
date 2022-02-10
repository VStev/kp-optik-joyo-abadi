package com.kp.optikjoyoabadi.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.kp.optikjoyoabadi.GlideApp
import com.kp.optikjoyoabadi.databinding.FragmentProfileBinding
import com.kp.optikjoyoabadi.ui.about.AboutActivity
import com.kp.optikjoyoabadi.ui.addresslist.AddressListActivity
import com.kp.optikjoyoabadi.ui.checkout.CheckoutViewModel
import com.kp.optikjoyoabadi.ui.editprofile.EditProfileActivity
import com.kp.optikjoyoabadi.ui.loginsignup.LoginActivity
import com.kp.optikjoyoabadi.ui.transactionlist.TransactionListActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment() {

    private val auth = Firebase.auth
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val profileViewModel: ProfileViewModel by viewModel()

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
            if (user.photoUrl != null){
                val image = Firebase.storage.reference.child("profiles/${user.photoUrl}")
                Log.d("TAG", "onViewCreated: ${user.photoUrl}")
                GlideApp.with(binding.root)
                    .load(image)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(binding.profilePic)
            }
            setonClickListeners()
        }else{
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
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
            val intent = Intent(context, EditProfileActivity::class.java)
            startActivity(intent)
        }

        binding.cardAbout.setOnClickListener {
            val intent = Intent(context, AboutActivity::class.java)
            startActivity(intent)
        }

        binding.buttonLogout.setOnClickListener {
            auth.signOut()
            profileViewModel.nuke()
            startActivity(Intent(context, LoginActivity::class.java))
        }
    }
}