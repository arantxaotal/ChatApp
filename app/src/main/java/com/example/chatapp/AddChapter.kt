package com.example.chatapp


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.recycleview.item.Book
import com.example.chatapp.recycleview.item.Chapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File

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
            val intent = Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(Intent.createChooser(intent, "Select Audio"), 111)

        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == RESULT_OK) {
            val path = data?.data
            path_view.text = path.toString()
        }

    }

    private fun crearCapitulo() {
        val db = FirebaseFirestore.getInstance()
        val booksRef = db.collection("Books").whereEqualTo("id", intent.getStringExtra("id")).get()
        val book = booksRef.result.documents[0].toObject(Book::class.java)
        nombre_capitulo = findViewById(R.id.namechapter)
        path_view = findViewById(R.id.path)
        val path = path_view.text.toString()
        var file = Uri.fromFile(File(path))
        val capitulo_nuevo = Chapter(nombre_capitulo.text.toString(),"audios/${file.lastPathSegment}",book!!.id)
        val ref = FirebaseStorage.getInstance().reference.child("audios/${file.lastPathSegment}")
        // CREA CAPITULO NOMBRE Y PATH
        database.child("Chapters").child(capitulo_nuevo.id).setValue(capitulo_nuevo)
        // SUBE ARCHIVO
        ref.putFile(file)


    }


}