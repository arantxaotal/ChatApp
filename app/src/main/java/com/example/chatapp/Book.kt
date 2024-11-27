package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Book : AppCompatActivity() {
    private lateinit var ver_libros : Button
    private lateinit var importar_capitulo : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book)
        ver_libros = findViewById(R.id.ver_libros_btn)
        ver_libros.setOnClickListener{
            val intent = Intent(this, Inicio::class.java)
            startActivity(intent)
        }
        importar_capitulo = findViewById(R.id.add_capitulo_btn)
        importar_capitulo.setOnClickListener{
            val intent = Intent(this, AddChapter::class.java)
            startActivity(intent)
        }
    }
}