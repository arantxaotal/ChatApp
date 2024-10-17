package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Registro : AppCompatActivity() {
    private lateinit var btn_atras : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        btn_atras = findViewById(R.id.atras_btn)

        btn_atras.setOnClickListener{
            val intent = Intent(this, Inicio::class.java)
            startActivity(intent)
        }

    }
}