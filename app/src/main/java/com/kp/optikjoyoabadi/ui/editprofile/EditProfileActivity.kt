package com.kp.optikjoyoabadi.ui.editprofile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.Timestamp
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import com.kp.optikjoyoabadi.GlideApp
import com.kp.optikjoyoabadi.R
import com.kp.optikjoyoabadi.databinding.ActivityEditProfileBinding
import com.kp.optikjoyoabadi.ui.loginsignup.LoginActivity

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private var paramName: String = "view"
    private var paramPwd: String = "view"
    private val auth = Firebase.auth
    private var name: String? = ""
    private lateinit var imageUri: Uri

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                val selectedImage: Uri? = it.data!!.data
                if (selectedImage != null) {
                    imageUri = selectedImage
                    binding.profilePic.setImageURI(imageUri)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Ubah profil"
        showLayout()
        setListeners()
    }

    private fun setListeners() {
        binding.buttonSaveEdit.setOnClickListener {
            when (paramName){
                "view" -> {
                    paramName = "edit"
                    binding.inputDisplayname.isEnabled = true
                    binding.buttonCancelEdit.visibility = View.VISIBLE
                    binding.buttonSaveEdit.setText(R.string.simpan)
                }
                "edit" -> {
                    paramName = "view"
                    name = binding.inputDisplayname.text.toString()
                    val user = auth.currentUser
                    val request = userProfileChangeRequest{
                        displayName = name
                    }
                    user?.updateProfile(request)
                    binding.inputDisplayname.isEnabled = false
                    binding.buttonSaveEdit.setText(R.string.ubah)
                }
            }
        }

        binding.buttonCancelEdit.setOnClickListener {
            binding.inputDisplayname.isEnabled = false
            binding.inputDisplayname.setText(name)
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
                    binding.buttonCancelEditPassword.visibility = View.VISIBLE
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
                                if (it.isSuccessful){
                                    auth.currentUser!!.updatePassword(newPassA)
                                        .addOnCompleteListener {
                                            //either relog or reauth
                                            Toast.makeText(this, "Password berashil dirubah, silahkan login kembali", Toast.LENGTH_SHORT).show()
                                            auth.signOut()
                                            startActivity(Intent(this, LoginActivity::class.java))
                                        }
                                }else{
                                    Toast.makeText(this, "Password lama anda salah!", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
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

        binding.pilihGamber.setOnClickListener {
            val options = arrayOf<CharSequence>("Choose from Gallery", "Cancel")
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Choose product picture")
            builder.setItems(options) { dialog, item ->
                when {
                    options[item] == "Choose from Gallery" -> {
                        val pickPhoto =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        resultLauncher.launch(pickPhoto)
                    }
                    options[item] == "Cancel" -> {
                        dialog.dismiss()
                    }
                }
            }
            builder.show()
        }

        binding.simpanGambar.setOnClickListener {
            val user = auth.currentUser
            val metadata = storageMetadata {
                contentType = "image/jpeg"
            }
            val filename = "PROFILE-${user?.uid}"
            val uploadTask = Firebase.storage.reference.child("profiles/${filename}").putFile(imageUri, metadata)
            uploadTask
                .addOnSuccessListener {
                    val request = userProfileChangeRequest{
                        photoUri = Uri.parse(filename)
                    }
                    user?.updateProfile(request)
                        ?.addOnSuccessListener {
                            Toast.makeText(this, "Foto Profil Berhasil Diubah", Toast.LENGTH_SHORT).show()
                        }
                }
        }
    }

    private fun showLayout() {
        binding.inputDisplayname.isEnabled = false
        binding.inputOldPassword.isEnabled = false
        binding.inputNewPasswordA.isEnabled = false
        binding.inputNewPasswordB.isEnabled = false
        val user = auth.currentUser
        name = user?.displayName
        if (user != null) {
            if (user.photoUrl != null){
                val image = Firebase.storage.reference.child("profiles/${user.photoUrl}")
                GlideApp.with(binding.root)
                    .load(image)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(binding.profilePic)
            }
        }
        binding.inputDisplayname.setText(name)
    }
}