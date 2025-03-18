package com.example.listascolaborativas.model

import android.os.Parcelable
import com.example.listascolaborativas.helper.FirebaseHelper
import kotlinx.parcelize.Parcelize

@Parcelize
data class Lista(
    var id: String = "",
    var titulo: String = ""
): Parcelable {
}

