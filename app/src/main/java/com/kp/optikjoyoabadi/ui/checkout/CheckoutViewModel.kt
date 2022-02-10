package com.kp.optikjoyoabadi.ui.checkout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.toObject
import com.kp.optikjoyoabadi.dataobjects.room.CartRepository
import com.kp.optikjoyoabadi.getFirebaseFirestoreInstance
import com.kp.optikjoyoabadi.model.Address
import com.kp.optikjoyoabadi.model.Cart
import com.kp.optikjoyoabadi.model.Product

class CheckoutViewModel(private val repository: CartRepository): ViewModel() {
    private val fireDB = getFirebaseFirestoreInstance()
    private val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    val cartItems = {
        repository.getCart()
    }

    fun insertToTransactionAndDetail(
        itemList: ArrayList<Cart>,
        address: Address,
        shipping: Int,
        subTotal: Int,
        uid: String,
        headerTitle: String,
        isBuyNow: Boolean
    ): LiveData<String> {
        val value = MutableLiveData<String>().apply{
            value = "onProcess"
        }
        var currentItem = 0
        val dateTime = Timestamp.now().toDate()
        val random = (1..13)
            .map { _ -> kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
        val transactionId = "INV-$random"
        val transactionData = hashMapOf(
            "transactionId" to transactionId,
            "UID" to uid,
            "headerTitle" to headerTitle,
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
                val paymentId = "PAY-$transactionId"
                val paymentData = hashMapOf(
                    "paymentId" to paymentId,
                    "transactionId" to transactionId,
                    "amount" to subTotal+shipping
                )
                fireDB.collection("Payment").document(paymentId)
                    .set(paymentData)
                itemList.forEach {
                    val transactionDetailData = hashMapOf(
                        "transactionId" to transactionId,
                        "productId" to it.productId,
                        "productName" to it.productName,
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
                if (!isBuyNow) repository.nukeCart()
                value.postValue("success")
            }
            .addOnFailureListener {
                value.postValue(it.message)
            }
        return value
    }
}