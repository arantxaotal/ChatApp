package com.example.chatapp.recycleview.item

import android.widget.TableRow

class BookData (row: TableRow, usuariouuid: String="", privado: Boolean=false, titulo: String="") {
    val row: TableRow = row
    val usuariouuid: String = usuariouuid
    val privado: Boolean = privado
    val titulo: String = titulo
}