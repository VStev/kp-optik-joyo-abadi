package com.kp.optikjoyoabadi

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import java.util.concurrent.Executors

const val NOTIFICATION_CHANNEL_NAME = "OJA_User"
const val NOTIFICATION_CHANNEL_ID = "notify-status"
const val NOTIFICATION_ID = 42
const val ID_REPEATING = 101

private val SINGLE_EXECUTOR = Executors.newSingleThreadExecutor()

fun executeThread(f: () -> Unit) {
    SINGLE_EXECUTOR.execute(f)
}

fun getFirebaseFirestoreInstance(): FirebaseFirestore {
    val db = Firebase.firestore
    val setting = firestoreSettings {
        isPersistenceEnabled = true
        cacheSizeBytes = 20000000
        build()
    }
    db.firestoreSettings = setting
    return db
}