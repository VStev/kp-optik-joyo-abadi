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

class CartAdapter: RecyclerView.Adapter<CartAdapter.CardViewHolder>() {

    private val mData = ArrayList<Cart>()
    private val reference = Firebase.storage.reference
    private lateinit var onItemClickCallback: OnItemClickCallback

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
                .override(256,256)
                .into(binding.productPictureThumb)
            binding.txtItemname.text = cartItem.productName
            binding.txtNote.text = cartItem.note
            binding.txtPrice.text = "Rp. ${cartItem.price}"
            binding.txtQuantity.text = cartItem.quantity.toString()
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ProductDetailActivity::class.java)
                intent.putExtra(ProductDetailActivity.EXTRA_ID, cartItem.productId)
                itemView.context.startActivity(intent)
            }
            binding.buttonDelete.setOnClickListener {
                onItemClickCallback.onItemClicked(cartItem, adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    override fun getItemCount(): Int = mData.size

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(product: Cart?, position: Int)
    }
}