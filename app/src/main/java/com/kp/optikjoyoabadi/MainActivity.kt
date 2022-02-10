package com.kp.optikjoyoabadi

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.kp.optikjoyoabadi.databinding.ActivityMainBinding
import com.kp.optikjoyoabadi.ui.addaddress.AddAddressActivity

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    private val fireDB = getFirebaseFirestoreInstance()
    private lateinit var argument: String
    private var fb = Firebase.dynamicLinks

    companion object {
        const val EXTRA_ARGUMENT = "old"
    }

    override fun onResume() {
        super.onResume()
        binding.navbarMain.selectedItemId = R.id.homeFragment //experimental basically sets screen to home screen in case someone cancels sign up process
        val logged = auth.currentUser
        if (logged != null) {
            logFCM(logged)
        }
        fb.getDynamicLink(intent)
            .addOnSuccessListener {
                var deepLink: Uri? = null
                if (it != null){
                    deepLink = it.link
                    val cred = logged?.email?.let { it1 ->
                        EmailAuthProvider.getCredentialWithLink(
                            it1, intent.data.toString())
                    }
                    if (cred != null) {
                        logged.linkWithCredential(cred)
                    }
                }
            }
            .addOnFailureListener(this) { e -> Log.w("TAG", "getDynamicLink:onFailure", e) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        val logged = auth.currentUser
        argument = intent.getStringExtra(EXTRA_ARGUMENT).toString()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        val navController = navHostFragment?.findNavController()
        if (navController != null) {
            binding.navbarMain.setupWithNavController(navController)
        }
        if (logged != null) {
            logFCM(logged)
            logged.reload()
            fb.getDynamicLink(intent)
                .addOnSuccessListener {
                    var deepLink: Uri? = null
                    if (it != null){
                        deepLink = it.link
                        val cred = logged.email?.let { it1 ->
                            EmailAuthProvider.getCredentialWithLink(
                                it1, intent.data.toString())
                        }
                        if (cred != null) {
                            logged.linkWithCredential(cred)
                        }
                    }
                }
                .addOnFailureListener(this) { e -> Log.w("TAG", "getDynamicLink:onFailure", e) }
        }
    }

    private fun logFCM(logged: FirebaseUser) {
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            val tokenized = getString(R.string.token_fmt, it)
            fireDB.collection("Users").document(logged.uid)
                    .update(
                            mapOf(
                                    "UID" to logged.uid,
                                    "FCMTOKEN" to tokenized
                            )
                    )
                    .addOnFailureListener {
                        Toast.makeText(baseContext, "Fail to register token", Toast.LENGTH_LONG).show()
                        auth.signOut()
                    }
        }
    }
}

