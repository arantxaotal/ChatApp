package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.recycleview.item.Book
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import java.text.SimpleDateFormat


class AddBook : AppCompatActivity() {

    private lateinit var create_btn: Button
    lateinit var titulo : EditText
    lateinit var autor : EditText
    lateinit var sinopsis : EditText
    lateinit var libro_nuevo : Book
    private lateinit var database: DatabaseReference
    private var libro_privado : Boolean = false
    private lateinit var libroPrivadoButton: MaterialCheckBox
    private var edit : Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book)
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        edit = intent.getBooleanExtra("edit", false)
        database = Firebase.database.reference
        create_btn = findViewById(R.id.crea_libro_btn)
        libroPrivadoButton = findViewById(R.id.private_book)
        libroPrivadoButton.setOnCheckedChangeListener { _, isChecked ->
            libro_privado = isChecked
        }
        create_btn.setOnClickListener {
            val success = crearLibro()
            if (success)
            {
                val intent = Intent(this, Inicio::class.java)
                startActivity(intent)
            }else
            {
                Toast.makeText(this, "Por favor, rellene todos los campos", Toast.LENGTH_SHORT).show()

            }

        }
        if(edit)
        {
            titulo = findViewById(R.id.titulo)
            autor = findViewById(R.id.autor)
            sinopsis = findViewById(R.id.sinopsis)
            titulo.setText(intent.getStringExtra("titulo"))
            autor.setText(intent.getStringExtra("autor"))
            sinopsis.setText(intent.getStringExtra("sinopsis"))
            if(intent.getBooleanExtra("privado", false))
            {
                libroPrivadoButton.isChecked = true
            }
            create_btn.text = "Actualizar"

        }



    }
    fun obtenerUIDUsuarioActual(): String? {
        val usuario = FirebaseAuth.getInstance().currentUser
        return usuario?.uid
    }

    private fun crearLibro(): Boolean {
        var success = true
        titulo = findViewById(R.id.titulo)
        autor = findViewById(R.id.autor)
        sinopsis = findViewById(R.id.sinopsis)
        val uid = obtenerUIDUsuarioActual()
        if(edit && intent.getStringExtra("id") != null)
        {
            if (titulo.text.toString().isEmpty() || autor.text.toString().isEmpty() || sinopsis.text.toString().isEmpty()) {
                success = false
            }else
            {
                val libro_update = database.child("Books").child(intent.getStringExtra("id").toString())
                // Data to update
                val updates = mapOf(
                    "titulo" to titulo.text.toString(),
                    "autor" to autor.text.toString(),
                    "sinopsis" to sinopsis.text.toString(),
                    "privado" to libro_privado
                )

                libro_update.updateChildren(updates)

            }
            return success
        }else
        {
            if (titulo.text.toString().isEmpty() || autor.text.toString().isEmpty() || sinopsis.text.toString().isEmpty()) {
                success = false
            }else
            {
                libro_nuevo = Book(titulo.text.toString(), autor.text.toString(), sinopsis.text.toString(), libro_privado, uid.toString())
                database.child("Books").child(libro_nuevo.id).setValue(libro_nuevo)

            }
            return success
        }



    }
}