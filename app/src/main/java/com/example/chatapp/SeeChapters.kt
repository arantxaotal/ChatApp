package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SeeChapters : AppCompatActivity() {
    private lateinit var crear_capitulo_btn : FloatingActionButton
    private lateinit var titulo_libro : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_see_chapters)
        crear_capitulo_btn = findViewById(R.id.crear_capitulo_btn)
        titulo_libro = findViewById(R.id.titulo_libro)
        titulo_libro.text = intent.getStringExtra("titulo")
        val id = intent.getStringExtra("id")

        crear_capitulo_btn.setOnClickListener{
            val intent = Intent(this, AddChapter::class.java)
            intent.putExtra("id", id)
            startActivity(intent)
        }

    }
}