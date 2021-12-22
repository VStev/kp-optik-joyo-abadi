package com.kp.optikjoyoabadi.ui.loginsignup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.FirebaseError.ERROR_INVALID_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kp.optikjoyoabadi.MainActivity
import com.kp.optikjoyoabadi.R
import com.kp.optikjoyoabadi.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private val auth = Firebase.auth
    private val fireDB = Firebase.firestore
    private lateinit var binding: ActivityLoginBinding
    private var retries = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()
    }

    private fun updateUI(user: FirebaseUser?){
        if (user != null) {
            var account: String
            //reference to document header, header of User always uses UID
            val reference = fireDB.collection("Users").document(user.uid)
            //when complete do this
            reference.get().addOnCompleteListener {
                //if success
                if (it.isSuccessful){
                    //gets the account type see if its Admin or not
                    account = it.result?.get("Type") as String
                    if (account != "User"){
                        //admin then no login
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }else{
                        //not admin then admit
                        Log.d("TAG", "signIn:success")
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                }else{
                    //when fail to get data
                    Log.w("TAG", "signIn:failure", it.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setListeners(){
        binding.buttonLogin.setOnClickListener {
            signIn()
        }

        binding.buttonSignup.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.buttonForgotPassword.setOnClickListener {
            val email = binding.inputLoginId.text.toString()
            if (email != ""){
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener {
                        Toast.makeText(baseContext, "Silahkan check email anda untuk reset password", Toast.LENGTH_SHORT).show()
                    }
            }else{
                Toast.makeText(baseContext, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signIn() {
        //if authentication is not banned then admit else nope
        auth.signInWithEmailAndPassword(binding.inputLoginId.text.toString(), binding.inputPassword.text.toString())
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signIn:failure", it.exception)
                    val error = it.exception?.message
                    if (retries > 3){
                        Toast.makeText(baseContext, "",
                                Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(baseContext, "$error",
                                Toast.LENGTH_SHORT).show()
                    }
                    if (it.exception?.equals(ERROR_INVALID_CREDENTIAL) == true){
                        retries++
                    }
                }
            }
    }
}