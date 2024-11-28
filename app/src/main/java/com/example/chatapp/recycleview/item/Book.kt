package com.example.chatapp.recycleview.item

import java.util.Date
import java.util.UUID

class Book(
    titulo: String = "",
    autor: String = "",
    sinopsis: String = ""
) {
    val id: String = UUID.randomUUID().toString()
    val titulo: String = titulo
    val autor: String = autor
    val sinopsis: String = sinopsis

}