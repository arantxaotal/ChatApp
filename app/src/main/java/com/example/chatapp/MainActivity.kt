package com.example.chatapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage


class MainActivity : AppCompatActivity() {
    private lateinit var list_view: ListView
    private lateinit var databaseRef: DatabaseReference
    private lateinit var add_button: FloatingActionButton
    private lateinit var book_table_view : TableLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fetchTable()
        add_button = findViewById(R.id.floatingActionButton)
        add_button.setOnClickListener {
            val intent = Intent(this, AddBook::class.java)
            startActivity(intent)
        }




    }

    private fun fetchTable() {
        databaseRef = FirebaseDatabase.getInstance().getReference("Books/")
        val query = databaseRef.orderByChild("titulo")
        book_table_view = findViewById(R.id.book_table)
        val usuario_uuid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val titulo = ds.child("titulo").getValue(String::class.java)
                    val autor = ds.child("autor").getValue(String::class.java)
                    val sinopsis = ds.child("sinopsis").getValue(String::class.java)
                    val id = ds.child("id").getValue(String::class.java)
                    val path = ds.child("path_image").getValue(String::class.java)
                    val privado = ds.child("privado").getValue(Boolean::class.java) == true
                    val tituloView = TextView(this@MainActivity)
                    val deleteButton = ImageButton(this@MainActivity)
                    deleteButton.setImageDrawable(ContextCompat.getDrawable(this@MainActivity, R.drawable.baseline_delete_outline_24))
                    val selectableBackground = TypedValue()
                    theme.resolveAttribute(android.R.attr.selectableItemBackground, selectableBackground, true)
                    deleteButton.setBackgroundResource(selectableBackground.resourceId)
                    val editButton = ImageButton(this@MainActivity)
                    editButton.setImageDrawable(ContextCompat.getDrawable(this@MainActivity, R.drawable.baseline_edit_24_purple))
                    editButton.setBackgroundResource(selectableBackground.resourceId)
                    editButton.setPadding(0, 60, 0, 0)
                    deleteButton.setPadding(0, 60, 0, 0)

                    val row = TableRow(this@MainActivity)

                    row.layoutParams = TableLayout.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT
                    )


                    // Set drawable on the left (icon resource)
                    tituloView.setCompoundDrawablesWithIntrinsicBounds(
                        R.mipmap.ic_book_round, // Left drawable
                        0, // Top drawable
                        0, // Right drawable
                        0  // Bottom drawable
                    )


                    tituloView.ellipsize = TextUtils.TruncateAt.END  // Truncate if text is too long
                    tituloView.maxLines = 1
                    tituloView.textSize = 20f
                    tituloView.gravity = Gravity.CENTER_VERTICAL
                    tituloView.text = titulo
                    tituloView.setPadding(10, 10, 10, 10)
                    tituloView.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                    if(usuario_uuid != ds.child("usuario_creador").getValue(String::class.java))
                    {
                        deleteButton.isEnabled = false
                        editButton.isEnabled = false
                    }


                    deleteButton.setOnClickListener() {

                        // Build the AlertDialog
                        val builder = AlertDialog.Builder(this@MainActivity)
                        builder.setTitle("Confirmar eliminación")
                            .setMessage("¿Deseas eliminar este libro?")
                            .setPositiveButton("Sí") { dialog, which ->
                                // User confirms deletion
                                val chapters = FirebaseDatabase.getInstance().getReference("Chapters/").orderByChild("book_id").equalTo(id).addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        for (data in snapshot.children) {
                                            val storageReference = FirebaseStorage.getInstance().reference
                                            val audioFileRef = storageReference.child("${data.child("path").value}")
                                            audioFileRef.delete()
                                            data.ref.removeValue()
                                        }
                                    }override fun onCancelled(error: DatabaseError) {
                                        println("Query cancelled: ${error.message}")
                                    }
                                })
                                databaseRef.child(id!!).removeValue()
                                val storageReference = FirebaseStorage.getInstance().reference
                                val imageFileRef = storageReference.child("${path}")
                                imageFileRef.delete()
                                book_table_view.removeView(row)
                                Toast.makeText(this@MainActivity, "Libro eliminado", Toast.LENGTH_SHORT).show()

                            }
                            .setNegativeButton("No") { dialog, which ->
                                // User cancels deletion
                                dialog.dismiss()
                            }
                            .create()
                            .show()


                    }

                    editButton.setOnClickListener()
                    {
                        val intent = Intent(this@MainActivity, AddBook::class.java)
                        intent.putExtra("id", id)
                        intent.putExtra("titulo", titulo)
                        intent.putExtra("autor", autor)
                        intent.putExtra("sinopsis", sinopsis)
                        intent.putExtra("edit", true)
                        intent.putExtra("usuario_uuid", usuario_uuid)
                        intent.putExtra("privado", privado)
                        intent.putExtra("path", path)
                        startActivity(intent)
                    }

                    tituloView.isClickable = true
                    tituloView.setOnClickListener {
                        val intent = Intent(this@MainActivity, WatchBook::class.java)
                        intent.putExtra("id", id)
                        intent.putExtra("titulo", titulo)
                        intent.putExtra("autor", autor)
                        intent.putExtra("sinopsis", sinopsis)
                        intent.putExtra("path", path)
                        startActivity(intent)

                    }
                    row.addView(tituloView)
                    row.addView(editButton)
                    row.addView(deleteButton)
                    book_table_view.addView(row)

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        query.addListenerForSingleValueEvent(eventListener)


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var Inflater = menuInflater
        Inflater.inflate(R.menu.menu_principal, menu)
        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_salir -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, Inicio::class.java)
                Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                startActivity(intent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}