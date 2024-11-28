package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.recycleview.item.Book
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import java.text.SimpleDateFormat


class AddBook : AppCompatActivity() {
    private lateinit var ver_libros : Button
    private lateinit var create_btn: Button
    lateinit var titulo : EditText
    lateinit var autor : EditText
    lateinit var sinopsis : EditText
    lateinit var libro_nuevo : Book
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        database = Firebase.database.reference
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book)
        create_btn = findViewById(R.id.crea_libro_btn)
        create_btn.setOnClickListener {
            titulo = findViewById(R.id.titulo)
            autor = findViewById(R.id.autor)
            sinopsis = findViewById(R.id.sinopsis); // Make sure user insert date into edittext in this format.
            libro_nuevo = Book(titulo.text.toString(), autor.text.toString(), sinopsis.text.toString())

            database.child("Books").child(libro_nuevo.id).setValue(libro_nuevo)
            val intent = Intent(this, WatchBook::class.java)
            startActivity(intent)
        }
        ver_libros = findViewById(R.id.ver_libros_btn)
        ver_libros.setOnClickListener{
            val intent = Intent(this, Inicio::class.java)
            startActivity(intent)
        }

    }
}