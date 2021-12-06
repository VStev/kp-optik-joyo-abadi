package com.kp.optikjoyoabadi

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.kp.optikjoyoabadi.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    private val fireDB = Firebase.firestore
    private lateinit var argument: String

    companion object {
        const val EXTRA_ARGUMENT = "old"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        val logged = auth.currentUser
        argument = intent.getStringExtra(EXTRA_ARGUMENT).toString()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
//        val navController = navHostFragment?.findNavController()
//        if (navController != null) {
//            binding.navbarMain.setupWithNavController(navController)
//        }
        if (logged != null){
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
            if (argument == "new"){
                val builder: AlertDialog.Builder = AlertDialog.Builder(this)

            }
        }
    }
}
