package com.example.listascolaborativas.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Lista(
    var id: String = "",
    var titulo: String = ""
) : Parcelable {
    // Construtor secundário necessário para Firebase
    constructor() : this("", "")
}
