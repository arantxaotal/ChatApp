package com.example.chatapp


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AddChapter : AppCompatActivity() {
    private lateinit var anadir_capitulo_btn : Button
    private lateinit var atras_btn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_chapter)
        anadir_capitulo_btn = findViewById(R.id.anadir_capitulo_btn)

        anadir_capitulo_btn.setOnClickListener {

        }
        atras_btn = findViewById(R.id.atras_btn_libro)
        atras_btn.setOnClickListener{
            val intent = Intent(this, WatchBook::class.java)
            startActivity(intent)
        }

    }
}