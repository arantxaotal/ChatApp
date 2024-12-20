package com.example.chatapp.recycleview.item

import android.provider.MediaStore.Audio
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class Chapter(path: String = "", book_id: String = "", nombre_capitulo: String = "", orden: Int = 0, usuario_creador: String = "") {
    val id: String = UUID.randomUUID().toString()
    val path: String  = path
    val nombre_capitulo: String = nombre_capitulo
    var book_id: String = book_id
    var orden: Int = orden
    // Get current date
    // Save the current date under a node
    val fecha_creacion = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date())
    var usuario_creador: String = usuario_creador



}