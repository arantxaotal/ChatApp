package com.example.chatapp


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import com.example.chatapp.recycleview.item.Chapter
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


class AddChapter : AppCompatActivity() {
    private lateinit var anadir_capitulo_btn : Button
    private lateinit var atras_btn : Button
    private lateinit var database : DatabaseReference
    lateinit var nombre_capitulo : TextView
    lateinit var path_view : TextView
    lateinit var subir_audio_btn : FloatingActionButton
    lateinit var name_file : String
    lateinit var path_file : String
    private var document_file : DocumentFile? = null
    lateinit var titulo : String
    lateinit var orden_capitulo : TextView
    private var edit : Boolean = false

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
        edit = intent.getBooleanExtra("edit", false)
        if(edit)
        {
            nombre_capitulo = findViewById(R.id.namechapter)
            orden_capitulo = findViewById(R.id.num_cap)
            path_view = findViewById(R.id.path)
            nombre_capitulo.text = intent.getStringExtra("titulo")
            orden_capitulo.text = intent.getStringExtra("orden")
            path_view.text = intent.getStringExtra("path")
            anadir_capitulo_btn.text = "Actualizar"


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

    private fun crearCapitulo() {
        val file_storage = FirebaseStorage.getInstance().reference
        val id =  intent.getStringExtra("id")
        path_view = findViewById(R.id.path)
        nombre_capitulo = findViewById(R.id.namechapter)
        orden_capitulo = findViewById(R.id.num_cap)

        if (nombre_capitulo.text.toString() == "")
        {
            Toast.makeText(this, "Nombre de capítulo vacío", Toast.LENGTH_SHORT).show()
        }
        if (path_view.text.toString() == "")
        {
            Toast.makeText(this, "Selecciona un audio", Toast.LENGTH_SHORT).show()
        }
        if (orden_capitulo.text.toString() == "")
        {
            Toast.makeText(this, "Orden de capítulo vacío", Toast.LENGTH_SHORT).show()
        }
        if (nombre_capitulo.text.toString() != "" && path_view.text.toString() != "" && orden_capitulo.text.toString() != "")
        {
            if(edit && intent.getStringExtra("id") != null && document_file == null ) {
                val add_chapter =
                    database.child("Chapters").child(intent.getStringExtra("id").toString())

                val updates = mapOf(
                    "nombre_capitulo" to nombre_capitulo.text.toString(),
                    "orden" to orden_capitulo.text.toString().toInt(),
                    "path" to path_view.text.toString()
                )

                val titulo_libro = intent.getStringExtra("titulo_libro")
                val autor_add = intent.getStringExtra("autor")
                val sinopsis_add = intent.getStringExtra("sinopsis")
                val book_id = intent.getStringExtra("book_id")


                add_chapter.updateChildren(updates)
                val intent = Intent(this@AddChapter, SeeChapters::class.java)
                intent.putExtra("titulo", titulo_libro)
                intent.putExtra("id", book_id)
                intent.putExtra("autor", autor_add)
                intent.putExtra("sinopsis", sinopsis_add)
                startActivity(intent)
            }
            else if(edit && intent.getStringExtra("id") != null && document_file != null) {
                val uri = document_file!!.uri
                val usuario_uuid = FirebaseAuth.getInstance().currentUser?.uid.toString()
                val capitulo_nuevo = Chapter("audios/${name_file}",id.toString(),nombre_capitulo.text.toString(),orden_capitulo.text.toString().toInt(), usuario_uuid)
                // SUBE ARCHIVO AL ALMACENAMIENTO EN LA NUBE
                val uploadTask = file_storage.child("audios/${name_file}").putFile(uri)
                uploadTask.addOnSuccessListener {
                    //GUARDA DATA EN BD DEL CAPITULO
                    val databaseReference = database.child("Chapters")
                    databaseReference.orderByChild("path").equalTo("audios/${name_file}")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists())
                                {

                                    Toast.makeText(this@AddChapter, "Audio existe en otro capítulo", Toast.LENGTH_SHORT).show()

                                }
                                else
                                {
                                    val add_chapter =
                                        database.child("Chapters").child(intent.getStringExtra("id").toString())

                                    val updates = mapOf(
                                        "nombre_capitulo" to nombre_capitulo.text.toString(),
                                        "orden" to orden_capitulo.text.toString().toInt(),
                                        "path" to path_view.text.toString()
                                    )

                                    add_chapter.updateChildren(updates)
                                    val intent = Intent(this@AddChapter, SeeChapters::class.java)
                                    intent.putExtra("titulo", intent.getStringExtra("titulo_libro"))
                                    intent.putExtra("id", intent.getStringExtra("book_id"))
                                    intent.putExtra("autor", intent.getStringExtra("autor"))
                                    intent.putExtra("sinopsis", intent.getStringExtra("sinopsis"))
                                    startActivity(intent)


                                }
                            }

                            override fun onCancelled( databaseError: DatabaseError) {
                            }
                        })


                }.addOnFailureListener {
                    Toast.makeText(this, "Error al subir el audio", Toast.LENGTH_SHORT).show()
                }
            }else
            {
                val uri = document_file?.uri
                val usuario_uuid = FirebaseAuth.getInstance().currentUser?.uid.toString()
                val capitulo_nuevo = Chapter("audios/${name_file}",id.toString(),nombre_capitulo.text.toString(),orden_capitulo.text.toString().toInt(), usuario_uuid)
                // SUBE ARCHIVO AL ALMACENAMIENTO EN LA NUBE
                val uploadTask = uri?.let { file_storage.child("audios/${name_file}").putFile(it) }
                if (uploadTask != null) {
                    uploadTask.addOnSuccessListener {
                        //GUARDA DATA EN BD DEL CAPITULO
                        val databaseReference = database.child("Chapters")
                        databaseReference.orderByChild("path").equalTo("audios/${name_file}")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    if (dataSnapshot.exists()) {

                                        Toast.makeText(this@AddChapter, "Audio existe en otro capítulo", Toast.LENGTH_SHORT).show()

                                    } else {

                                        database.child("Chapters").child(capitulo_nuevo.id).setValue(capitulo_nuevo)
                                        Toast.makeText(this@AddChapter, "Audio subido correctamente", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this@AddChapter, SeeChapters::class.java)
                                        intent.putExtra("titulo", intent.getStringExtra("titulo_libro"))
                                        intent.putExtra("id", intent.getStringExtra("book_id"))
                                        intent.putExtra("autor", intent.getStringExtra("autor"))
                                        intent.putExtra("sinopsis", intent.getStringExtra("sinopsis"))
                                        startActivity(intent)


                                    }
                                }

                                override fun onCancelled( databaseError: DatabaseError) {
                                }
                            })


                    }.addOnFailureListener {
                        Toast.makeText(this, "Error al subir el audio", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }


    }


}