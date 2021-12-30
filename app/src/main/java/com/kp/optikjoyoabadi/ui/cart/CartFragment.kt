package com.kp.optikjoyoabadi.ui.cart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kp.optikjoyoabadi.R
import com.kp.optikjoyoabadi.adapters.CartAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.kp.optikjoyoabadi.databinding.FragmentCartBinding

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var cardAdapter: CartAdapter
    private val cartViewModel: CartViewModel by viewModel()
    private lateinit var rv: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv = view.findViewById(R.id.recycler_cart)
        updateUI()
    }

    private fun updateUI(){
        cardAdapter = CartAdapter()
        cartViewModel.cartItems().observe(viewLifecycleOwner, {Cart ->
            if (Cart != null){
                when (Cart.size){
                    0 -> {
                        binding.recyclerCart.visibility = View.GONE
                        binding.subtotalCard.visibility = View.GONE
                        binding.emptyCartLayout.visibility = View.VISIBLE
                    }
                    else ->{
                        cardAdapter.setData(Cart)
                        var total = 0
                        Cart.forEach{
                            total += (it.price * it.quantity)
                        }
                        val subText = "Rp. $total"
                        binding.txtSubtotal.text = subText
                        setListeners()
                    }
                }
                with(rv){
                    layoutManager = LinearLayoutManager(context)
                    adapter = cardAdapter
                }
            }
        })
    }

    private fun setListeners() {
        binding.buttonCheckout.setOnClickListener {
            //intent to checkout
        }
    }
}