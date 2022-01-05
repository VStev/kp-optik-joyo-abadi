package com.kp.optikjoyoabadi.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class Cart (
    @PrimaryKey
    @ColumnInfo(name = "productId")
    @NonNull
    var productId: String = "",

    @ColumnInfo(name = "productName")
    @NonNull
    var productName: String = "",

    @ColumnInfo(name = "category")
    @NonNull
    val category: String = "",

    @ColumnInfo(name = "shape")
    @NonNull
    val shape: String = "",

    @ColumnInfo(name = "details")
    @NonNull
    val details: String = "",

    @ColumnInfo(name = "note")
    var note: String = "",

    @ColumnInfo(name = "price")
    @NonNull
    var price: Int = 0,

    @ColumnInfo(name = "quantity")
    @NonNull
    var quantity: Int = 0,

    @ColumnInfo(name = "image_url")
    @NonNull
    var image_url: String = ""
        )