package com.kp.optikjoyoabadi.model

import com.google.firebase.Timestamp

data class Transaction(
    val transactionId: String = "",
    val consumerId: String = "",
    val recipientName: String = "",
    val street: String = "",
    val city: String = "",
    val region: String = "",
    val postalCode: Int = 0,
    val phoneNumber: Int = 0,
    val shippingNumber: String? = "",
    val status: String = "",
    val subtotal: Int = 0,
    val shippingFee: Int = 0,
    val total: Int = 0,
    val dateTime: Timestamp = Timestamp.now()
)