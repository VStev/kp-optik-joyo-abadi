package com.kp.optikjoyoabadi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.kp.optikjoyoabadi.databinding.ActivityMainBinding
import com.kp.optikjoyoabadi.ui.loginsignup.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    private val fireDB = Firebase.firestore

    companion object {
        const val EXTRA_ARGUMENT = "old"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        val logged = auth.currentUser
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
//        val navController = navHostFragment?.findNavController()
//        if (navController != null) {
//            binding.navbarMain.setupWithNavController(navController)
//        }
        if (logged == null){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }else{
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
                    }
            }
        }
    }
}
