package com.kp.optikjoyoabadi.ui.loginsignup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.kp.optikjoyoabadi.MainActivity
import com.kp.optikjoyoabadi.R
import com.kp.optikjoyoabadi.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private val auth = Firebase.auth
    private val fireDB = Firebase.firestore
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()
    }

    private fun updateUI(user: FirebaseUser){
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener {
            val tokenized = getString(R.string.token_fmt, it)
            fireDB.collection("Users").document(user.uid)
                .set(
                    mapOf(
                        "UID" to user.uid,
                        "FCMTOKEN" to tokenized,
                        "Type" to "User"
                    )
                )
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra(MainActivity.EXTRA_ARGUMENT, "new")
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(baseContext, "Fail to register token", Toast.LENGTH_LONG).show()
            }
    }

    private fun setListeners(){
        binding.buttonLogin.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.buttonSignup.setOnClickListener {
            signUp()
        }
    }

    private fun signUp() {
        val email = binding.inputEmail.text.toString()
        if (email == ""){
            Toast.makeText(baseContext, "Email tidak boleh kosong!", Toast.LENGTH_SHORT).show()
        }else{
            if (binding.inputPassword.text.toString() == binding.inputConfirmPassword.text.toString()){
                val password = binding.inputPassword.text.toString()
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            val user = auth.currentUser
                            user?.sendEmailVerification()
                            if (user != null) {
                                updateUI(user)
                            }
                        }
                    }
            }else if (binding.inputPassword.text.isNullOrBlank() || binding.inputPassword.text.isNullOrEmpty()
                || binding.inputConfirmPassword.text.isNullOrBlank() || binding.inputConfirmPassword.text.isNullOrEmpty()){
                Toast.makeText(baseContext, "Password tidak boleh kosong!", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(baseContext, "Password tidak sama",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
}