package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    private lateinit var btn_atras : Button
    private lateinit var btn_iniciar_sesion : Button
    private lateinit var email_text : EditText
    private lateinit var password : EditText
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        btn_atras = findViewById(R.id.atras_btn)
        btn_iniciar_sesion = findViewById(R.id.iniciar_sesion_btn)

        btn_atras.setOnClickListener{
            val intent = Intent(this, Inicio::class.java)
            startActivity(intent)
        }

        email_text = findViewById(R.id.email)
        password = findViewById(R.id.password)
        firebaseAuth = FirebaseAuth.getInstance()

        btn_iniciar_sesion.setOnClickListener{

            firebaseAuth.signInWithEmailAndPassword(email_text.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information

                        val user = firebaseAuth.currentUser
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        Toast.makeText(this, "Sesión iniciada", Toast.LENGTH_SHORT).show()
                    } else {
                        // If sign in fails, display a message to the user.

                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }

        }

    }
}