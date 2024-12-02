package com.example.chatapp


import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.chatapp.recycleview.item.Book
import com.example.chatapp.recycleview.item.Chapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.storageMetadata
import java.io.File
import java.io.FileInputStream

class AddChapter : AppCompatActivity() {
    private lateinit var anadir_capitulo_btn : Button
    private lateinit var atras_btn : Button
    private lateinit var database : DatabaseReference
    lateinit var nombre_capitulo : TextView
    lateinit var path_view : TextView
    lateinit var subir_audio_btn : FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        database = Firebase.database.reference
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_chapter)
        anadir_capitulo_btn = findViewById(R.id.anadir_capitulo_btn)
        anadir_capitulo_btn.setOnClickListener {
            crearCapitulo()

        }
        subir_audio_btn = findViewById(R.id.subir_audio_btn)
        subir_audio_btn.setOnClickListener{
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.setType("*/*")
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(Intent.createChooser(intent, "Select Audio"), 111)

        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == RESULT_OK) {
            val path = data?.data
            path_view = findViewById(R.id.path)
            path_view.text = path.toString()

        }

    }

    private fun crearCapitulo() {
        val file_storage = FirebaseStorage.getInstance().reference
        val id =  intent.getStringExtra("id")
        path_view = findViewById(R.id.path)
        val path = path_view.text.toString()
        var file = File(path)
        var uri = Uri.fromFile(file)
        nombre_capitulo = findViewById<TextView>(R.id.namechapter)
        val capitulo_nuevo = Chapter("audios/${uri.lastPathSegment}.mp3",id.toString(),nombre_capitulo.text.toString())
        // Create file metadata including the content type
        var metadata = storageMetadata {
            contentType = "audio/mp3"
        }

        // SUBE ARCHIVO AL ALMACENAMIENTO EN LA NUBE
        val uploadTask = file_storage.child("audios/${uri.lastPathSegment}.mp3").putFile(uri,metadata)
        uploadTask.addOnSuccessListener {
            //GUARDA DATA EN BD DEL CAPITULO
            database.child("Chapters").child(capitulo_nuevo.id).setValue(capitulo_nuevo)
            Toast.makeText(this, "Audio subido correctamente", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Error al subir el audio", Toast.LENGTH_SHORT).show()
        }

    }


}