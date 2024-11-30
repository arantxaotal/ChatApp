package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


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
        book_table_view = findViewById(R.id.book_table)
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val titulo = ds.child("titulo").getValue(String::class.java)
                    val autor = ds.child("autor").getValue(String::class.java)
                    val sinopsis = ds.child("sinopsis").getValue(String::class.java)
                    val id = ds.key
                    val tituloView = TextView(this@MainActivity)
                    val deleteButton = FloatingActionButton(this@MainActivity)
                    val row = TableRow(this@MainActivity)

                    row.layoutParams = TableLayout.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT
                    )
                    deleteButton.setImageDrawable(ContextCompat.getDrawable(this@MainActivity, R.drawable.baseline_delete_outline_24))
                    row.setPadding(16, 16, 16, 16)
                    tituloView.text = titulo
                    tituloView.textSize = 20f
                    tituloView.setPadding(16, 16, 16, 16)
                    tituloView.layoutParams = TableRow.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT)

                    deleteButton.setOnClickListener {
                        databaseRef.child(id!!).removeValue()
                        book_table_view.removeView(row)
                        Toast.makeText(this@MainActivity, "Libro eliminado", Toast.LENGTH_SHORT).show()

                    }

                    tituloView.isClickable = true
                    tituloView.setOnClickListener {
                        val intent = Intent(this@MainActivity, WatchBook::class.java)
                        intent.putExtra("id", id)
                        intent.putExtra("titulo", titulo)
                        intent.putExtra("autor", autor)
                        intent.putExtra("sinopsis", sinopsis)
                        startActivity(intent)

                    }
                    row.addView(tituloView)
                    row.addView(deleteButton)
                    book_table_view.addView(row)

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        databaseRef.addListenerForSingleValueEvent(eventListener)


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