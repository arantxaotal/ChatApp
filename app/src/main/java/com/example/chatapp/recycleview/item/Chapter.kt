package com.example.chatapp.recycleview.item

import android.provider.MediaStore.Audio
import java.util.UUID

class Chapter(nombre: String = "", path: String = "", book_id: String = "") {
    val id: String = UUID.randomUUID().toString()
    val nombre: String = nombre
    val path: String  = path
    var book_id: String = book_id

}