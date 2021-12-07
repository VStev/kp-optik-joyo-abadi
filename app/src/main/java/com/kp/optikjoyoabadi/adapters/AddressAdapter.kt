package com.kp.optikjoyoabadi.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.kp.optikjoyoabadi.databinding.ItemAddressBinding
import com.kp.optikjoyoabadi.model.Address

open class AddressAdapter(query: Query?): FirestoreAdapter<AddressAdapter.CardViewHolder>(query) {

    inner class CardViewHolder(private val items: ItemAddressBinding) : RecyclerView.ViewHolder(items.root) {
        fun bind(data: DocumentSnapshot) {
            val addressDetail = data.toObject<Address>()
            Log.d("bind:", "$addressDetail")
            //revised
            val street = "${addressDetail?.street}  ${addressDetail?.city}"
            items.txtRecipientName.text = addressDetail?.recipientName
            items.txtStreet.text = street
            items.txtPhoneNumber.text = addressDetail?.phoneNumber
            if (addressDetail != null) {
                when (addressDetail.isMain){
                    true -> {
                        items.txtAddressIsmain.text = "Main"
                        items.txtAddressIsmain.visibility = View.VISIBLE
                    }
                    false -> {
                        items.txtAddressIsmain.text = ""
                        items.txtAddressIsmain.visibility = View.GONE
                    }
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder(ItemAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(getSnapshot(position))
    }
}