package com.kp.optikjoyoabadi.ui.profile

import androidx.lifecycle.ViewModel
import com.kp.optikjoyoabadi.dataobjects.room.CartRepository

class ProfileViewModel(private val repository: CartRepository): ViewModel() {
    fun nuke() = repository.nukeCart()
}