package com.example.chatapp


import android.R.attr
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import com.example.chatapp.recycleview.item.Chapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storageMetadata
import java.io.File


class AddChapter : AppCompatActivity() {
    private lateinit var anadir_capitulo_btn : Button
    private lateinit var atras_btn : Button
    private lateinit var database : DatabaseReference
    lateinit var nombre_capitulo : TextView
    lateinit var path_view : TextView
    lateinit var subir_audio_btn : FloatingActionButton
    lateinit var name_file : String
    lateinit var path_file : String
    lateinit var document_file : DocumentFile
    lateinit var titulo : String

    override fun onCreate(savedInstanceState: Bundle?) {
        titulo = intent.getStringExtra("titulo").toString()
        database = Firebase.database.reference
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_chapter)
        anadir_capitulo_btn = findViewById(R.id.anadir_capitulo_btn)
        anadir_capitulo_btn.setOnClickListener {
            crearCapitulo()

        }
        subir_audio_btn = findViewById(R.id.subir_audio_btn)
        subir_audio_btn.setOnClickListener{
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.setType("*/*")
            //intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(Intent.createChooser(intent, "Select Audio"), 111)

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
                    name_file = document_file.name.toString()
                    path_file = document_file.uri.path.toString()
                    path_view = findViewById(R.id.path)
                    path_view.text = name_file
                }
            }

        }


    }

    private fun crearCapitulo() {
        val file_storage = FirebaseStorage.getInstance().reference
        val id =  intent.getStringExtra("id")
        path_view = findViewById(R.id.path)
        var uri = document_file.uri
        nombre_capitulo = findViewById(R.id.namechapter)
        val capitulo_nuevo = Chapter("audios/${name_file}",id.toString(),nombre_capitulo.text.toString())



        // SUBE ARCHIVO AL ALMACENAMIENTO EN LA NUBE
        val uploadTask = file_storage.child("audios/${name_file}").putFile(uri)
        uploadTask.addOnSuccessListener {
            //GUARDA DATA EN BD DEL CAPITULO
            database.child("Chapters").child(capitulo_nuevo.id).setValue(capitulo_nuevo)
            Toast.makeText(this, "Audio subido correctamente", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SeeChapters::class.java)
            intent.putExtra("titulo", titulo)
            intent.putExtra("id", id)
            startActivity(intent)

        }.addOnFailureListener {
            Toast.makeText(this, "Error al subir el audio", Toast.LENGTH_SHORT).show()
        }

    }


}