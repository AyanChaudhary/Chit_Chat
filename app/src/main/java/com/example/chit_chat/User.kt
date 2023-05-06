package com.example.chit_chat

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val imageUrl: String="",
    val name: String="",
    val uid: String="") : Parcelable

