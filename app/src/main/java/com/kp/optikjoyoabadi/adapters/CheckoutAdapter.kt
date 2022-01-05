package com.kp.optikjoyoabadi.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.kp.optikjoyoabadi.GlideApp
import com.kp.optikjoyoabadi.R
import com.kp.optikjoyoabadi.databinding.ItemCartBinding
import com.kp.optikjoyoabadi.model.Cart
import com.kp.optikjoyoabadi.ui.productdetail.ProductDetailActivity

class CheckoutAdapter: RecyclerView.Adapter<CheckoutAdapter.CardViewHolder>() {

    private val mData = ArrayList<Cart>()
    private val reference = Firebase.storage.reference
    private lateinit var onItemClickCallback: CartAdapter.OnItemClickCallback

    fun setData(items: List<Cart>) {
        mData.clear()
        mData.addAll(items)
        notifyDataSetChanged()
    }

    inner class CardViewHolder(items: View) : RecyclerView.ViewHolder(items) {
        private val binding = ItemCartBinding.bind(itemView)
        fun bind(cartItem: Cart) {
            val image = reference.child("products/${cartItem.image_url}")
            GlideApp.with(binding.root)
                .load(image)
                .into(binding.productPictureThumb)
            binding.txtItemname.text = cartItem.productName
            binding.txtNote.text = cartItem.note
            binding.txtPrice.text = cartItem.price.toString()
            binding.txtQuantity.text = cartItem.quantity.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_transaction_details, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    override fun getItemCount(): Int = mData.size
}