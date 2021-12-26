package com.kp.optikjoyoabadi.ui.addaddress

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kp.optikjoyoabadi.model.Address

class AddAddressViewModel : ViewModel() {
    private val fireDB = Firebase.firestore

    fun submitData(address: Address) {
        val data = hashMapOf(
            "addressId" to address.addressId,
            "consumerId" to address.consumerId,
            "recipientName" to address.recipientName,
            "street" to address.street,
            "region" to address.region,
            "city" to address.city,
            "postalCode" to address.postalCode,
            "phoneNumber" to address.phoneNumber,
            "isMain" to address.isMain
        )
        fireDB.collection("Address").document(address.addressId)
            .set(data)
            .addOnSuccessListener {

            }
            .addOnFailureListener {

            }
    }

    fun submitUpdate(address: Address) {
        val data = hashMapOf(
            "addressId" to address.addressId,
            "consumerId" to address.consumerId,
            "recipientName" to address.recipientName,
            "street" to address.street,
            "region" to address.region,
            "city" to address.city,
            "postalCode" to address.postalCode,
            "phoneNumber" to address.phoneNumber,
            "isMain" to address.isMain
        )
        fireDB.collection("Address").document(address.addressId)
            .update(data as Map<String, Any>)
    }
}