package com.kp.optikjoyoabadi.ui.checkout

import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.kp.optikjoyoabadi.dataobjects.room.CartRepository
import com.kp.optikjoyoabadi.getFirebaseFirestoreInstance
import com.kp.optikjoyoabadi.model.Address
import com.kp.optikjoyoabadi.model.Cart

class CheckoutViewModel(private val repository: CartRepository): ViewModel() {
    private val fireDB = getFirebaseFirestoreInstance()

    val cartItems = {
        repository.getCart()
    }

    fun insertToTransactionAndDetail(
        itemList: ArrayList<Cart>,
        address: Address,
        shipping: Int,
        subTotal: Int,
        uid: String
    ) {
        var currentItem = 0
        val transactionId = "INV/$uid/${Timestamp.now().toDate()}"
        val transactionData = hashMapOf(
            "transactionId" to transactionId,
            "consumerId" to uid,
            "recipientName" to address.recipientName,
            "street" to address.street,
            "city" to address.city,
            "region" to address.region,
            "postalCode" to address.postalCode,
            "phoneNumber" to address.phoneNumber,
            "shippingNumber" to "",
            "status" to "WAITING FOR PAYMENT",
            "subTotal" to subTotal,
            "shippingFee" to shipping,
            "total" to subTotal+shipping,
            "dateTime" to Timestamp.now()
        )
        fireDB.collection("Transactions").document(transactionId)
            .set(transactionData)
            .addOnSuccessListener {
                itemList.forEach {
                    val transactionDetailData = hashMapOf(
                        "transactionId" to transactionId,
                        "productId" to it.productId,
                        "productName" to it.productName,
                        "details" to it.details,
                        "category" to it.category,
                        "shape" to it.shape,
                        "price" to it.price,
                        "notes" to it.note,
                        "quantity" to it.quantity,
                        "image_url" to it.image_url
                    )
                    val doc = "$transactionId+$currentItem"
                    fireDB.collection("TransactionDetail").document(doc)
                        .set(transactionDetailData)
                    currentItem += 1
                }
            }
            .addOnFailureListener {

            }
    }
}