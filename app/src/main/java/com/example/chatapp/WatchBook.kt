package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.w3c.dom.Text

class WatchBook(
) : AppCompatActivity() {
    private lateinit var ver_capitulos_btn : Button
    private lateinit var titulo : TextView
    private lateinit var autor : TextView
    private lateinit var sinopsis : TextView
    private lateinit var id : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_watch_book)
        titulo = findViewById(R.id.titulo_texto)
        titulo.text = intent.getStringExtra("titulo")
        autor = findViewById(R.id.autor_texto)
        autor.text = intent.getStringExtra("autor")
        sinopsis = findViewById(R.id.sinopsis_texto)
        sinopsis.text = intent.getStringExtra("sinopsis")
        id = intent.getStringExtra("id").toString()

        ver_capitulos_btn = findViewById(R.id.ver_capitulos_btn)
        ver_capitulos_btn.setOnClickListener{
            val intent = Intent(this, SeeChapters::class.java)
            intent.putExtra("titulo", titulo.text.toString())
            intent.putExtra("id", id)
            startActivity(intent)
        }
    }
}