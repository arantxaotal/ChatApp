package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class WatchBook(
) : AppCompatActivity() {
    private lateinit var volver_btn : Button
    private lateinit var ver_capitulos_btn : Button
    private lateinit var libroNombre : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_watch_book)
        libroNombre = findViewById(R.id.libro_nombre)
        libroNombre.text = intent.getStringExtra("libro_nombre")

        ver_capitulos_btn = findViewById(R.id.ver_capitulos_btn)
        ver_capitulos_btn.setOnClickListener{
            val intent = Intent(this, SeeChapters::class.java)
            intent.putExtra("libro_nombre", libroNombre.text.toString())
            startActivity(intent)
        }
        volver_btn = findViewById(R.id.volver_btn)
        volver_btn.setOnClickListener{
            val intent = Intent(this, Inicio::class.java)
            startActivity(intent)
        }
    }
}