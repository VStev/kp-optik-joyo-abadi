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
import com.kp.optikjoyoabadi.databinding.ItemChangeAddressBinding
import com.kp.optikjoyoabadi.model.Address
import com.kp.optikjoyoabadi.model.Cart
import com.kp.optikjoyoabadi.ui.productdetail.ProductDetailActivity

class ChangeAddressAdapter: RecyclerView.Adapter<ChangeAddressAdapter.CardViewHolder>() {

    private val mData = ArrayList<Address>()
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setData(items: List<Address>) {
        mData.clear()
        mData.addAll(items)
        notifyDataSetChanged()
    }

    inner class CardViewHolder(items: View) : RecyclerView.ViewHolder(items) {
        private val binding = ItemChangeAddressBinding.bind(itemView)
        fun bind(addressItem: Address) {
            binding.txtRecipientName.text = addressItem.recipientName
            val street = "${addressItem.street}  ${addressItem.city}"
            binding.txtStreet.text = street
            binding.txtPhoneNumber.text = addressItem.phoneNumber
            binding.buttonChooseAddress.setOnClickListener { onItemClickCallback.onItemClicked(adapterPosition) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_change_address, parent, false)
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
        fun onItemClicked(position: Int)
    }
}