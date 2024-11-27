package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AddBook : AppCompatActivity() {
    private lateinit var ver_libros : Button
    private lateinit var create_btn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book)
        create_btn = findViewById(R.id.crea_libro_btn)
        create_btn.setOnClickListener {
            val intent = Intent(this, Book::class.java)
            startActivity(intent)
        }
        ver_libros = findViewById(R.id.ver_libros_btn)
        ver_libros.setOnClickListener{
            val intent = Intent(this, Inicio::class.java)
            startActivity(intent)
        }

    }
}