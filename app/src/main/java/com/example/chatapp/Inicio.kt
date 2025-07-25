package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class Inicio : AppCompatActivity() {

    private lateinit var btn_login : Button
    private lateinit var btn_registro : Button
    var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)
        btn_login = findViewById(R.id.ingresar_btn)
        btn_registro = findViewById(R.id.registro_btn)

        btn_login.setOnClickListener{
            val intent = Intent(this, Login::class.java)
            startActivity(intent)

        }
        btn_registro.setOnClickListener{
            val intent = Intent(this, Registro::class.java)
            startActivity(intent)

        }
    }

    private fun ComprobarSesion() {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        ComprobarSesion()
    }
}