package com.kp.optikjoyoabadi.model

data class Address(
    val addressId: String = "",
    val UID: String = "",
    val recipientName: String = "",
    val street: String = "",
    val region: String = "",
    val city: String = "",
    val postalCode: Int = 0,
    val phoneNumber: String = "",
    val main: Boolean = false
)

