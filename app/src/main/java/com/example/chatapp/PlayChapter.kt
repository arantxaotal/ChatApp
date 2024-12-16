package com.example.chatapp

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PlayChapter : AppCompatActivity() {
    private lateinit var titulo_cap : TextView
    private lateinit var audioPath : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_chapter)
        titulo_cap = findViewById(R.id.titulo_cap)
        titulo_cap.text = "Capitulo: " + intent.getStringExtra("titulo")
        audioPath = findViewById(R.id.audioPath)
        audioPath.text = intent.getStringExtra("audioPath")



    }
}