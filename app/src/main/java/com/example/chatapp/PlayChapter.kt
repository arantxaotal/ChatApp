package com.example.chatapp

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class PlayChapter : AppCompatActivity() {
    private lateinit var titulo_cap : TextView
    private lateinit var audioPath : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_chapter)
        titulo_cap = findViewById(R.id.titulo_cap)
        titulo_cap.text = intent.getStringExtra("titulo")
        audioPath = findViewById(R.id.audioPath)
        val titulo_path = intent.getStringExtra("path").toString().split("audios/")[1]
        audioPath.text = titulo_path
        getDataAudio(titulo_path)

    }
    private fun getDataAudio(path: String) {

    }
}