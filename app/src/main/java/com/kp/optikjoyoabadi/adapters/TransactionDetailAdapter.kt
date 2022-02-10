package com.kp.optikjoyoabadi.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.StorageReference
import com.kp.optikjoyoabadi.GlideApp
import com.kp.optikjoyoabadi.databinding.ItemTransactionDetailsBinding
import com.kp.optikjoyoabadi.model.TransactionDetail

open class TransactionDetailAdapter(query: Query, private val reference: StorageReference): FirestoreAdapter<TransactionDetailAdapter.CardViewHolder>(query) {
    //revised

    inner class CardViewHolder(private val items: ItemTransactionDetailsBinding) : RecyclerView.ViewHolder(items.root) {
        fun bind(data: DocumentSnapshot) {
            val transactionDetail = data.toObject<TransactionDetail>()
            items.txtNote.text = transactionDetail?.notes
            items.txtQuantity.text = transactionDetail?.quantity.toString()
            items.txtItemname.text = transactionDetail?.productName
            items.txtPrice.text = "Rp. ${transactionDetail?.price.toString()}"
            val image = transactionDetail?.let { reference.child("products/${it.image_url}") }
            GlideApp.with(items.root)
                .load(image)
                .override(256,256)
                .into(items.productPictureThumb)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder(ItemTransactionDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(getSnapshot(position))
    }
}