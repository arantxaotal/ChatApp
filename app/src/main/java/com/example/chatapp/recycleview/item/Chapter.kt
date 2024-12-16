package com.example.chatapp.recycleview.item

import android.provider.MediaStore.Audio
import java.util.UUID

class Chapter(path: String = "", book_id: String = "", nombre_capitulo: String = "", orden: Int = 0) {
    val id: String = UUID.randomUUID().toString()
    val path: String  = path
    val nombre_capitulo: String = nombre_capitulo
    var book_id: String = book_id
    var orden: Int = orden



}