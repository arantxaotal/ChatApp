package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage

class SeeChapters : AppCompatActivity() {
    private lateinit var crear_capitulo_btn : FloatingActionButton
    private lateinit var titulo_libro : TextView
    private lateinit var databaseRef: DatabaseReference
    private lateinit var chapter_table_view : TableLayout
    private val storage = FirebaseStorage.getInstance()
    private lateinit var id_libro : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_see_chapters)
        crear_capitulo_btn = findViewById(R.id.crear_capitulo_btn)
        titulo_libro = findViewById(R.id.titulo_libro)
        titulo_libro.text = intent.getStringExtra("titulo")
        val id = intent.getStringExtra("id")
        id_libro = id!!
        fetchTable()

        crear_capitulo_btn.setOnClickListener{
            val intent = Intent(this, AddChapter::class.java)
            intent.putExtra("id", id)
            startActivity(intent)
        }

    }
    private fun fetchTable() {
        databaseRef = FirebaseDatabase.getInstance().getReference("Chapters/")
        val query = databaseRef.orderByChild("book_id").equalTo(id_libro)
        chapter_table_view = findViewById(R.id.chapter_table)
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val nombre_capitulo = ds.child("nombre_capitulo").getValue(String::class.java)
                    val audio_path = ds.child("path").getValue(String::class.java)
                    val book_id = ds.child("book_id").getValue(String::class.java)
                    val id = ds.child("id").getValue(String::class.java)
                    val nombre_capitulo_view = TextView(this@SeeChapters)
                    val deleteButton = FloatingActionButton(this@SeeChapters)
                    val row = TableRow(this@SeeChapters)

                    row.layoutParams = TableLayout.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT
                    )
                    deleteButton.setImageDrawable(ContextCompat.getDrawable(this@SeeChapters, R.drawable.baseline_delete_outline_24))
                    row.setPadding(16, 16, 16, 16)
                    nombre_capitulo_view.text = nombre_capitulo
                    nombre_capitulo_view.textSize = 20f
                    nombre_capitulo_view.setPadding(16, 16, 16, 16)
                    nombre_capitulo_view.layoutParams = TableRow.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT)

                    deleteButton.setOnClickListener {
                        if (audio_path != null)
                        {
                            val storageRef = storage.reference

                            val desertRef = storageRef.child(audio_path)
                            // Delete the file
                            desertRef.delete().addOnSuccessListener {
                                databaseRef.child(id!!).removeValue()
                                chapter_table_view.removeView(row)
                                Toast.makeText(this@SeeChapters, "Capítulo eliminado", Toast.LENGTH_SHORT).show()
                            }.addOnFailureListener {
                                Toast.makeText(this@SeeChapters, "Error al eliminar el capítulo", Toast.LENGTH_SHORT).show()
                            }
                        }


                    }

                    nombre_capitulo_view.isClickable = true
                    nombre_capitulo_view.setOnClickListener {


                    }
                    row.addView(nombre_capitulo_view)
                    row.addView(deleteButton)
                    chapter_table_view.addView(row)

                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        }

        query.addListenerForSingleValueEvent(eventListener)


    }
}

