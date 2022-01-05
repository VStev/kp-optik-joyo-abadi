package com.kp.optikjoyoabadi.ui.editprofile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.kp.optikjoyoabadi.R
import com.kp.optikjoyoabadi.databinding.ActivityEditProfileBinding
import com.kp.optikjoyoabadi.getFirebaseFirestoreInstance
import com.kp.optikjoyoabadi.model.User

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private var paramName: String = "view"
    private var paramPwd: String = "view"
    private val auth = Firebase.auth
    private val fireDB = getFirebaseFirestoreInstance()
    private var name: String? = ""
    private var phoneNumber: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showLayout()
        setListeners()
    }

    private fun setListeners() {
        binding.buttonSaveEdit.setOnClickListener {
            when (paramName){
                "view" -> {
                    paramName = "edit"
                    binding.inputDisplayname.isEnabled = true
                    binding.inputTelephone.isEnabled = true
                    binding.buttonCancelEdit.visibility = View.VISIBLE
                    binding.buttonSaveEdit.setText(R.string.simpan)
                }
                "edit" -> {
                    paramName = "view"
                    name = binding.inputDisplayname.text.toString()
                    phoneNumber = binding.inputTelephone.text.toString()
                    val user = auth.currentUser
                    val request = userProfileChangeRequest{
                        displayName = name
                    }
                    user?.updateProfile(request)
                    if (user != null) {
                        fireDB.collection("Users").document(user.uid)
                                .update(
                                        mapOf(
                                                "phoneNumber" to phoneNumber
                                        )
                                )
                    }
                    binding.inputDisplayname.isEnabled = false
                    binding.inputTelephone.isEnabled = false
                    binding.buttonSaveEdit.setText(R.string.ubah)
                }
            }
        }

        binding.buttonCancelEdit.setOnClickListener {
            binding.inputDisplayname.isEnabled = false
            binding.inputTelephone.isEnabled = false
            binding.inputDisplayname.setText(name)
            binding.inputTelephone.setText(phoneNumber)
            binding.buttonCancelEdit.visibility = View.GONE
            binding.buttonSaveEdit.setText(R.string.ubah)
        }

        binding.buttonSaveEditPassword.setOnClickListener {
            when(paramPwd){
                "view" -> {
                    paramPwd = "edit"
                    binding.inputOldPassword.isEnabled = true
                    binding.inputNewPasswordA.isEnabled = true
                    binding.inputNewPasswordB.isEnabled = true
                    binding.buttonSaveEditPassword.setText(R.string.simpan)
                }
                "edit" -> {
                    paramPwd = "view"
                    val user = auth.currentUser
                    val email = user?.email.toString()
                    val oldPass = binding.inputOldPassword.text.toString()
                    val newPassA = binding.inputNewPasswordA.text.toString()
                    val newPassB = binding.inputNewPasswordB.text.toString()
                    val credential = EmailAuthProvider
                            .getCredential(email, oldPass)
                    if (newPassA != newPassB) {
                        Toast.makeText(applicationContext, "Password Tidak Sama!", Toast.LENGTH_SHORT).show()
                    }else{
                        auth.currentUser!!.reauthenticate(credential)
                                .addOnCompleteListener {
                                    auth.currentUser!!.updatePassword(newPassA)
                                            .addOnCompleteListener {
                                                //either relog or reauth
                                                Toast.makeText(applicationContext, "Password berashil dirubah, silahkan login kembali", Toast.LENGTH_SHORT).show()
                                            }
                                }
                    }
                    binding.inputOldPassword.setText("")
                    binding.inputNewPasswordA.setText("")
                    binding.inputNewPasswordB.setText("")
                    binding.inputOldPassword.isEnabled = false
                    binding.inputNewPasswordA.isEnabled = false
                    binding.inputNewPasswordB.isEnabled = false
                    binding.buttonSaveEditPassword.setText(R.string.ubah)
                }
            }
        }

        binding.buttonCancelEditPassword.setOnClickListener {
            binding.inputOldPassword.setText("")
            binding.inputNewPasswordA.setText("")
            binding.inputNewPasswordB.setText("")
            binding.inputOldPassword.isEnabled = false
            binding.inputNewPasswordA.isEnabled = false
            binding.inputNewPasswordB.isEnabled = false
            binding.buttonCancelEditPassword.visibility = View.GONE
            binding.buttonSaveEditPassword.setText(R.string.ubah)
        }
    }

    private fun showLayout() {
        binding.inputDisplayname.isEnabled = false
        binding.inputTelephone.isEnabled = false
        binding.inputOldPassword.isEnabled = false
        binding.inputNewPasswordA.isEnabled = false
        binding.inputNewPasswordB.isEnabled = false
        val user = auth.currentUser
        val query = user?.let { fireDB.collection("Users").document(it.uid) }
        name = user?.displayName
        binding.inputDisplayname.setText(name)
        query?.get()?.addOnSuccessListener {
            val userObject = it.toObject<User>()
            phoneNumber = userObject?.phoneNumber
            binding.inputTelephone.setText(phoneNumber)
        }
    }
}