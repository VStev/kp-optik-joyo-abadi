package com.kp.optikjoyoabadi.ui.addaddress

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.kp.optikjoyoabadi.R
import com.kp.optikjoyoabadi.databinding.ActivityAddAddressBinding
import com.kp.optikjoyoabadi.getFirebaseFirestoreInstance
import com.kp.optikjoyoabadi.model.Address
import com.kp.optikjoyoabadi.ui.addresslist.AddressListActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.random.Random

class AddAddressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddAddressBinding
    private lateinit var parameter: String
    private lateinit var editAddressId: String
    private val fireDB = getFirebaseFirestoreInstance()
    private val auth = Firebase.auth.currentUser
    private val addViewModel: AddAddressViewModel by viewModel()

    companion object {
        const val EXTRA_PARAM = "new"
        const val EXTRA_ID = "id1234"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAddressBinding.inflate(layoutInflater)
        parameter = intent.getStringExtra(EXTRA_PARAM).toString()
        editAddressId = intent.getStringExtra(EXTRA_ID).toString()
        title = "Tambah Alamat"
        setContentView(binding.root)
        showLayout()
        setListeners()
    }

    private fun setListeners() {
        binding.buttonSaveAddress.setOnClickListener {
            val name = binding.addRecipientName.text.toString()
            val street = binding.addAddressStreet.text.toString()
            val region = binding.addKabupaten.selectedItem.toString()
            val postalCode =
                if (binding.addPostalCode.text.toString() != "") binding.addPostalCode.text.toString()
                    .toInt() else 0
            val city = binding.addCity.text.toString()
            val phoneNumber = binding.addRecipientPhone.text.toString()
            when (parameter) {
                "edit" -> {
                    val addressId = editAddressId
                    val address = auth?.let {
                        Address(
                            addressId,
                            it.uid,
                            name,
                            street,
                            region,
                            city,
                            postalCode,
                            phoneNumber,
                            false
                        )
                    }
                    if (address != null) {
                        addViewModel.submitUpdate(address).observe(this,{
                            if (it == "Success"){
                                Toast.makeText(
                                    baseContext,
                                    getString(R.string.update_address_success),
                                    Toast.LENGTH_LONG
                                ).show()
                                startActivity(Intent(this, AddressListActivity::class.java))
                                finish()
                            }else{
                                Toast.makeText(
                                    baseContext,
                                    it,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        })
                    }
                }
                "new" -> {
                    val randomNumber = Random.nextInt(0, 1000)
                    val randomized =
                        when {
                            randomNumber < 10 -> {
                                "000$randomNumber"
                            }
                            randomNumber < 100 -> {
                                "00$randomNumber"
                            }
                            randomNumber < 1000 -> {
                                "0$randomNumber"
                            }
                            else -> {
                                randomNumber.toString()
                            }
                        }
                    val addressId =
                        "ADDR" + auth?.uid?.substring(0, 5) + randomized + auth?.uid?.substring(
                            5,
                            5
                        )
                    val address = auth?.let {
                        Address(
                            addressId,
                            it.uid,
                            name,
                            street,
                            region,
                            city,
                            postalCode,
                            phoneNumber,
                            false
                        )
                    }
                    if (address != null) {
                        addViewModel.submitData(address).observe(this,{
                            if (it == "Success"){
                                Toast.makeText(
                                    baseContext,
                                    getString(R.string.add_address_success),
                                    Toast.LENGTH_LONG
                                ).show()
                                startActivity(Intent(this, AddressListActivity::class.java))
                                finish()
                            }else{
                                Toast.makeText(
                                    baseContext,
                                    it,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        })
                    }
                }
                "first" -> {
                    val randomNumber = Random.nextInt(0, 1000)
                    val randomized =
                        when {
                            randomNumber < 10 -> {
                                "000$randomNumber"
                            }
                            randomNumber < 100 -> {
                                "00$randomNumber"
                            }
                            randomNumber < 1000 -> {
                                "0$randomNumber"
                            }
                            else -> {
                                randomNumber.toString()
                            }
                        }
                    val addressId =
                        "ADDR" + auth?.uid?.substring(0, 5) + randomized + auth?.uid?.substring(
                            5,
                            5
                        )
                    val address = auth?.let {
                        Address(
                            addressId,
                            it.uid,
                            name,
                            street,
                            region,
                            city,
                            postalCode,
                            phoneNumber,
                            true
                        )
                    }
                    if (address != null) {
                        addViewModel.submitData(address).observe(this,{
                            if (it == "Success"){
                                Toast.makeText(
                                    baseContext,
                                    getString(R.string.add_address_success),
                                    Toast.LENGTH_LONG
                                ).show()
                                startActivity(Intent(this, AddressListActivity::class.java))
                                finish()
                            }else{
                                Toast.makeText(
                                    baseContext,
                                    it,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        })
                    }
                }
            }
        }
    }

    private fun showLayout() {
        Log.d("TAG", "showLayout: $parameter")
        Log.d("TAG", "showLayout: $editAddressId")
        title = "Ubah Alamat"
        when (parameter) {
            "edit" -> {
                val query = fireDB.collection("Address").document(editAddressId)
                query.get()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val address = it.result?.toObject<Address>()
                            val regionArray = resources.getStringArray(R.array.Region)
                            binding.addRecipientName.setText(address?.recipientName)
                            binding.addAddressStreet.setText(address?.street)
                            binding.addCity.setText(address?.city)
                            binding.addPostalCode.setText(address?.postalCode.toString())
                            binding.addRecipientPhone.setText(address?.phoneNumber)
                            regionArray.forEachIndexed { index, s ->
                                if (address != null) {
                                    if (address.region == s){
                                        binding.addKabupaten.setSelection(index)
                                    }
                                }
                            }
                        }
                    }
            }
        }
    }
}