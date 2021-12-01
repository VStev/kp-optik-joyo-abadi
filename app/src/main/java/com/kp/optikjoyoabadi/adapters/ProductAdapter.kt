package com.kp.optikjoyoabadi.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.StorageReference
import com.kp.optikjoyoabadi.GlideApp
import com.kp.optikjoyoabadi.databinding.ItemProductsBinding
import com.kp.optikjoyoabadi.model.Product

open class ProductAdapter(query: Query, private val reference: StorageReference): FirestoreAdapter<ProductAdapter.CardViewHolder>(query) {

    inner class CardViewHolder(private val items: ItemProductsBinding) : RecyclerView.ViewHolder(items.root) {
        fun bind(data: DocumentSnapshot) {
            val product = data.toObject<Product>()
            Log.d("bind:", "$product")
            items.txtProductName.text = product?.productName
            items.txtProdCategory.text = product?.category
            items.txtProductPrice.text = "Rp. ${product?.price.toString()}"
            val image = product?.let { reference.child("products/${it.image_url}") }
            GlideApp.with(items.root)
                .load(image)
                .override(256,256)
                .into(items.productPictureThumb)
//            items.root.setOnClickListener {
//                val intent = Intent(items.root.context, AddEditProductActivity::class.java)
//                intent.putExtra(AddEditProductActivity.EXTRA_ID, product?.productId)
//                intent.putExtra(AddEditProductActivity.EXTRA_ARGUMENT, "b")
//                items.root.context.startActivity(intent)
//            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder(ItemProductsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(getSnapshot(position))
    }
}