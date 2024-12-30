package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Registro : AppCompatActivity() {
    private lateinit var btn_atras : ImageButton
    private lateinit var nombre : EditText
    private lateinit var apellidos : EditText
    private lateinit var email_text : EditText
    private lateinit var password : EditText
    private lateinit var password_repeat : EditText
    private lateinit var telefono : EditText
    private lateinit var crear_cuenta_btn : Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        btn_atras = findViewById(R.id.atras_btn)
        btn_atras.setOnClickListener{
            val intent = Intent(this, Inicio::class.java)
            startActivity(intent)
        }
        InicializarVariables()
        crear_cuenta_btn.setOnClickListener {
            ValidarDatos()
        }


    }

    private fun InicializarVariables()
    {
        nombre = findViewById(R.id.nombre)
        apellidos = findViewById(R.id.apellidos)
        email_text = findViewById(R.id.email)
        password = findViewById(R.id.password)
        password_repeat = findViewById(R.id.password_repeat)
        telefono = findViewById(R.id.telefono)
        crear_cuenta_btn = findViewById(R.id.crear_cuenta_btn)
        auth = FirebaseAuth.getInstance()

    }

    private fun ValidarDatos()
    {
        val nombre = nombre.text.toString()
        val apellidos = apellidos.text.toString()
        val email = email_text.text.toString()
        val password = password.text.toString()
        val password_repeat = password_repeat.text.toString()
        val telefono = telefono.text.toString()
        if(nombre.isEmpty())
        {
            Toast.makeText(this, "Por favor, complete su nombre", Toast.LENGTH_SHORT).show()
        }
        else if(apellidos.isEmpty())
        {
            Toast.makeText(this, "Por favor, complete sus apellidos", Toast.LENGTH_SHORT).show()
        }
        else if(email.isEmpty())
        {
            Toast.makeText(this, "Por favor, complete su email", Toast.LENGTH_SHORT).show()
        }
        else if(password.isEmpty())
        {
            Toast.makeText(this, "Por favor, complete su contraseña", Toast.LENGTH_SHORT).show()
        }
        else if(password_repeat.isEmpty() || password_repeat != password)
        {
            Toast.makeText(this, "Por favor, repita su contraseña", Toast.LENGTH_SHORT).show()
        }
        else if(telefono.isEmpty())
        {
            Toast.makeText(this, "Por favor, complete su teléfono", Toast.LENGTH_SHORT).show()
        }
        else
        {
            RegistrarUsuario(email, password)
        }
    }

    private fun RegistrarUsuario(email: String, password: String)
    {
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registro exitoso
                    val user = auth.currentUser
                    var uid: String = ""
                    uid = user!!.uid
                    val reference = FirebaseDatabase.getInstance().getReference("Users").child(uid)
                    val hashMap: HashMap<String, Any> = HashMap()
                    val h_nombre_usuario: String = nombre.text.toString()
                    val h_email_usuario: String = email_text.text.toString()
                    val h_apellidos_usuario: String = apellidos.text.toString()
                    val h_telefono_usuario: String = telefono.text.toString()


                    hashMap["uid"] = uid
                    hashMap["nombre_usuario"] = h_nombre_usuario
                    hashMap["apellidos_usuario"] = h_apellidos_usuario
                    hashMap["telefono_usuario"] = h_telefono_usuario
                    hashMap["email_usuario"] = h_email_usuario
                    hashMap["imagen"] = ""
                    hashMap["buscar"] = h_nombre_usuario.lowercase()

                    reference.updateChildren(hashMap).addOnCompleteListener { task2 ->
                        if (task2.isSuccessful) {
                            val intent = Intent(this, Inicio::class.java)
                            startActivity(intent)
                            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()

                        }
                    }.addOnFailureListener { e ->
                        Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                    }


                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    //
                }else
                {
                    // Si el registro falla, se muestra un mensaje al usuario
                    Toast.makeText(this, "Error en el registro", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener{ e ->
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
    }
}