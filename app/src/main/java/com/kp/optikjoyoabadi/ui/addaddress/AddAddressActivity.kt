package com.kp.optikjoyoabadi.ui.addaddress

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.kp.optikjoyoabadi.databinding.ActivityAddAddressBinding
import com.kp.optikjoyoabadi.getFirebaseFirestoreInstance
import com.kp.optikjoyoabadi.model.Address
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
        const val EXTRA_ID = "new"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAddressBinding.inflate(layoutInflater)
        parameter = intent.getStringExtra(EXTRA_PARAM).toString()
        editAddressId = intent.getStringExtra(EXTRA_ID).toString()
        setContentView(binding.root)
        showLayout()
        setListeners()
    }

    private fun setListeners() {
        when (parameter){
            "edit" -> {
                val name = binding.addRecipientName.text.toString()
                val street = binding.addAddressStreet.text.toString()
                val region = binding.addKabupaten.selectedItem.toString()
                val postalCode = binding.addPostalCode.text.toString()
                val city = binding.addCity.text.toString()
                val phoneNumber = binding.addRecipientPhone.text.toString()
                val addressId = editAddressId
                val address = auth?.let {
                    Address(
                        addressId,
                        it.uid,
                        name,
                        street,
                        region,
                        city,
                        postalCode.toInt(),
                        phoneNumber,
                        false
                    )
                }
                if (address != null) {
                    addViewModel.submitUpdate(address)
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
                val name = binding.addRecipientName.text.toString()
                val street = binding.addAddressStreet.text.toString()
                val region = binding.addKabupaten.selectedItem.toString()
                val postalCode = binding.addPostalCode.text.toString()
                val city = binding.addCity.text.toString()
                val phoneNumber = binding.addRecipientPhone.text.toString()
                val addressId = "ADDR" + auth?.uid?.substring(0, 5) + randomized + auth?.uid?.substring(5, 5)
                val address = auth?.let {
                    Address(
                        addressId,
                        it.uid,
                        name,
                        street,
                        region,
                        city,
                        postalCode.toInt(),
                        phoneNumber,
                        false
                        )
                }
                if (address != null) {
                    addViewModel.submitData(address)
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
                val name = binding.addRecipientName.text.toString()
                val street = binding.addAddressStreet.text.toString()
                val region = binding.addKabupaten.selectedItem.toString()
                val postalCode = binding.addPostalCode.text.toString()
                val city = binding.addCity.text.toString()
                val phoneNumber = binding.addRecipientPhone.text.toString()
                val addressId = "ADDR" + auth?.uid?.substring(0, 5) + randomized + auth?.uid?.substring(5, 5)
                val address = auth?.let {
                    Address(
                        addressId,
                        it.uid,
                        name,
                        street,
                        region,
                        city,
                        postalCode.toInt(),
                        phoneNumber,
                        true
                    )
                }
                if (address != null) {
                    addViewModel.submitData(address)
                }
            }
        }
    }

    private fun showLayout() {
        when (parameter){
            "edit" -> {
                val query = fireDB.collection("Address").document(editAddressId)
                query.get()
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            //TODO(match region info with array)
                            val address = it.result?.toObject<Address>()
                            binding.addRecipientName.setText(address?.recipientName)
                            binding.addAddressStreet.setText(address?.street)
                            binding.addCity.setText(address?.city)
                            binding.addPostalCode.setText(address?.postalCode.toString())
                            binding.addRecipientPhone.setText(address?.phoneNumber)
                        }
                    }
            }
        }
    }
}