package com.kp.optikjoyoabadi.ui.homescreen

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kp.optikjoyoabadi.R
import com.kp.optikjoyoabadi.databinding.FragmentHomeBinding
import com.kp.optikjoyoabadi.ui.productlist.ProductListActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var auth = Firebase.auth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = auth.currentUser
        if (user != null) {
            binding.welcomeText.text = getString(R.string.welcome_text, user.displayName)
        }
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding.cardKacamata.setOnClickListener {
            val intent = Intent(context, ProductListActivity::class.java)
            intent.putExtra(ProductListActivity.EXTRA_ARGUMENT, "kacamata")
            startActivity(intent)
        }

        binding.cardSunglasses.setOnClickListener {
            val intent = Intent(context, ProductListActivity::class.java)
            intent.putExtra(ProductListActivity.EXTRA_ARGUMENT, "sunglasses")
            startActivity(intent)
        }

        binding.cardAksesoris.setOnClickListener {
            val intent = Intent(context, ProductListActivity::class.java)
            intent.putExtra(ProductListActivity.EXTRA_ARGUMENT, "aksesoris")
            startActivity(intent)
        }
    }
}