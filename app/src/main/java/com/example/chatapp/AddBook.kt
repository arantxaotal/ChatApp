package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import com.example.chatapp.recycleview.item.Book
import com.example.chatapp.recycleview.item.Chapter
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage
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
    lateinit var subir_portada_btn : FloatingActionButton
    lateinit var name_file : String
    lateinit var path_file : String
    private var document_file : DocumentFile? = null
    lateinit var path_view : TextView


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
                val intent = Intent(this, MainActivity::class.java)
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
        subir_portada_btn = findViewById(R.id.subir_portada_btn)
        subir_portada_btn.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Select Portada"), 111)

        }



        }
        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == 111 && resultCode == RESULT_OK) {
                val fileUri = data?.data
                if (fileUri != null)
                {
                    document_file = DocumentFile.fromSingleUri(this, fileUri)!!
                    if (document_file != null) {
                        name_file = document_file!!.name.toString()
                        path_file = document_file!!.uri.path.toString()
                        path_view = findViewById(R.id.path)
                        path_view.text = name_file
                    }
                }

            }



    }
    fun obtenerUIDUsuarioActual(): String? {
        val usuario = FirebaseAuth.getInstance().currentUser
        return usuario?.uid
    }

    private fun crearLibro(): Boolean {
        val file_storage = FirebaseStorage.getInstance().reference
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
        }else
        {
            if (titulo.text.toString().isEmpty() || autor.text.toString().isEmpty() || sinopsis.text.toString().isEmpty()) {
                success = false
            }else
            {
                val uri = document_file?.uri
                val usuario_uuid = FirebaseAuth.getInstance().currentUser?.uid.toString()
                libro_nuevo = Book(titulo.text.toString(), autor.text.toString(), sinopsis.text.toString(), libro_privado,usuario_uuid,"portadas/${name_file}")
                // SUBE ARCHIVO AL ALMACENAMIENTO EN LA NUBE
                val uploadTask = uri?.let { file_storage.child("portadas/${name_file}").putFile(it) }
                if (uploadTask != null) {
                    uploadTask.addOnSuccessListener {
                        //GUARDA DATA EN BD de la portada
                        val databaseReference = database.child("Books")
                        databaseReference.orderByChild("path").equalTo("portadas/${name_file}")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    if (dataSnapshot.exists()) {

                                        Toast.makeText(this@AddBook, "Portada existe en otro libro", Toast.LENGTH_SHORT).show()
                                        success = false
                                    } else {
                                        database.child("Books").child(libro_nuevo.id).setValue(libro_nuevo)
                                        Toast.makeText(this@AddBook, "Libro subido correctamente", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onCancelled( databaseError: DatabaseError) {
                                    success = false
                                    Toast.makeText(this@AddBook, "Error al subir portada", Toast.LENGTH_SHORT).show()

                                }
                            })


                    }.addOnFailureListener {
                        success = false
                        Toast.makeText(this, "Error al subir portada", Toast.LENGTH_SHORT).show()
                    }
                }else
                {
                    success = false
                    Toast.makeText(this, "Error al subir portada", Toast.LENGTH_SHORT).show()
                }



            }


        }
        return success




    }
}