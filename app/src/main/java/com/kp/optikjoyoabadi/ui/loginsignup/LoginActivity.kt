package com.kp.optikjoyoabadi.ui.loginsignup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseError
import com.google.firebase.FirebaseError.ERROR_INVALID_CREDENTIAL
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
        supportActionBar?.hide()
        setListeners()
    }

    override fun onResume() {
        super.onResume()
        if (auth.currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun updateUI(user: FirebaseUser?){
        if (user != null) {
            //reference to document header, header of User always uses UID
            val reference = fireDB.collection("Users").document(user.uid)
            //when complete do this
            reference.get().addOnCompleteListener {
                //if success
                if (it.isSuccessful){
                    //gets the account type see if its Admin or not
                    val account = it.result?.get("Type") as String
                    if (account != "User"){
                        //admin then no login
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                        auth.signOut()
                    }else{
                        //not admin then admit
                        Log.d("TAG", "signIn:success")
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
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
            if (binding.inputLoginId.text.toString().isEmpty()
                || binding.inputLoginId.text.toString().isBlank()
                || binding.inputPassword.text.toString().isEmpty()
                || binding.inputPassword.text.toString().isBlank()){
                Toast.makeText(baseContext, getString(R.string.invalid_input_msg), Toast.LENGTH_SHORT).show()
            }else{
                signIn()
            }
        }

        binding.buttonSignup.setOnClickListener {
            binding.inputLoginId.setText("")
            binding.inputPassword.setText("")
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.buttonForgotPassword.setOnClickListener {
            val email = binding.inputLoginId.text.toString()
            if (email != ""){
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener {
                        Toast.makeText(baseContext, getString(R.string.reset_pwd_msg), Toast.LENGTH_SHORT).show()
                    }
            }else{
                Toast.makeText(baseContext, getString(R.string.empty_email_msg), Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(baseContext, getString(R.string.please_reset_pwd_msg),
                                Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(baseContext, "$error",
                                Toast.LENGTH_SHORT).show()
                    }
                    retries++
                }
            }
    }
}