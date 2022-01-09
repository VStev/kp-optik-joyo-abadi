package com.kp.optikjoyoabadi.ui.paymentdetail

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import com.kp.optikjoyoabadi.getFirebaseFirestoreInstance

class PaymentDetailViewModel: ViewModel() {

    private val fireDb = getFirebaseFirestoreInstance()
    private val storage = Firebase.storage.reference

    fun submitData(id:String, image: Uri): LiveData<String> {
        val metadata = storageMetadata {
            contentType = "image/jpeg"
        }
        val value = MutableLiveData<String>().apply{
            value = "onProcess"
        }
        val filename = "PROOF/$id"
        val uploadTask = storage.child("payments/${filename}").putFile(image, metadata)
        val newData = mapOf(
            "proof" to filename,
            "receivedAt" to Timestamp.now()
        )
        uploadTask
            .addOnSuccessListener {
                fireDb.collection("Payment").document(id)
                    .update(newData)
                    .addOnSuccessListener {
                        value.value = "Success!"
                    }
            }
            .removeOnFailureListener {
                value.value = "Failed!"
            }
        return value
    }
}