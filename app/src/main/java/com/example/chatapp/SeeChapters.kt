package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SeeChapters : AppCompatActivity() {
    private lateinit var volver : Button
    private lateinit var crear_capitulo_btn :Button
    private lateinit var titulo_libro : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_see_chapters)
        crear_capitulo_btn = findViewById(R.id.crear_capitulo_btn)
        volver = findViewById(R.id.volver_btn)
        titulo_libro = findViewById(R.id.titulo_libro)
        titulo_libro.text = intent.getStringExtra("libro_nombre")

        crear_capitulo_btn.setOnClickListener{
            val intent = Intent(this, AddChapter::class.java)
            intent.putExtra("libro_nombre", titulo_libro.text.toString());
            startActivity(intent)
        }
        volver.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }
}