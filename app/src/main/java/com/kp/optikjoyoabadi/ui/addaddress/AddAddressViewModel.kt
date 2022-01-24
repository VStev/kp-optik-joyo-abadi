package com.kp.optikjoyoabadi.ui.addaddress

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kp.optikjoyoabadi.getFirebaseFirestoreInstance
import com.kp.optikjoyoabadi.model.Address

class AddAddressViewModel : ViewModel() {
    private val fireDB = getFirebaseFirestoreInstance()

    fun submitData(address: Address): LiveData<String> {
        val value = MutableLiveData<String>().apply{
            value = "onProcess"
        }
        val data = hashMapOf(
            "addressId" to address.addressId,
            "UID" to address.UID,
            "recipientName" to address.recipientName,
            "street" to address.street,
            "region" to address.region,
            "city" to address.city,
            "postalCode" to address.postalCode,
            "phoneNumber" to address.phoneNumber,
            "main" to address.main
        )
        fireDB.collection("Address").document(address.addressId)
            .set(data)
            .addOnSuccessListener {
                value.postValue("Success")
            }
            .addOnFailureListener {
                value.postValue(it.message)
            }
        return value
    }

    fun submitUpdate(address: Address): LiveData<String> {
        val value = MutableLiveData<String>().apply{
            value = "onProcess"
        }
        val data = hashMapOf(
            "addressId" to address.addressId,
            "UID" to address.UID,
            "recipientName" to address.recipientName,
            "street" to address.street,
            "region" to address.region,
            "city" to address.city,
            "postalCode" to address.postalCode,
            "phoneNumber" to address.phoneNumber,
            "main" to address.main
        )
        fireDB.collection("Address").document(address.addressId)
            .update(data as Map<String, Any>)
            .addOnSuccessListener {
                value.postValue("Success")
            }
            .addOnFailureListener {
                value.postValue(it.message)
            }
        return value
    }
}