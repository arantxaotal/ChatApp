package com.example.chatapp.recycleview.item

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class Book(
    titulo: String = "",
    autor: String = "",
    sinopsis: String = "",
    privado: Boolean = false,
    usuario_creador: String = ""
) {
    val id: String = UUID.randomUUID().toString()
    val titulo: String = titulo
    val autor: String = autor
    val sinopsis: String = sinopsis
    val privado: Boolean = privado
    // Get current date
    // Save the current date under a node
    val fecha_creacion = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date())
    val usuario_creador: String = usuario_creador


}