package com.kp.optikjoyoabadi.adapters

import android.content.Intent
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
import com.kp.optikjoyoabadi.ui.addaddress.AddAddressActivity

open class AddressAdapter(query: Query?): FirestoreAdapter<AddressAdapter.CardViewHolder>(query) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    inner class CardViewHolder(private val items: ItemAddressBinding) : RecyclerView.ViewHolder(items.root) {
        fun bind(data: DocumentSnapshot) {
            val addressDetail = data.toObject<Address>()
            Log.d("bind:", "$addressDetail")
            //revised
            val addressId = addressDetail?.addressId
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
            items.buttonMakeMain.setOnClickListener {
                onItemClickCallback.onItemClicked(addressId)
            }
            items.buttonEditAddress.setOnClickListener {
                val intent = Intent(items.root.context, AddAddressActivity::class.java)
                intent.putExtra(AddAddressActivity.EXTRA_PARAM, "edit")
                items.root.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder(ItemAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(getSnapshot(position))
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(addressId: String?)
    }
}