package com.example.listascolaborativas.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Item(
    var id: String = "",
    var nome: String = "",
    var listaId: String = ""
) : Parcelable
