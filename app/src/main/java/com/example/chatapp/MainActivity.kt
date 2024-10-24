package com.example.chatapp

import android.R.array
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MainActivity : AppCompatActivity() {
    private lateinit var list_view: ListView
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fetchUsers()
        list_view.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, Conversation::class.java)
            startActivity(intent)
        }




    }

    private fun fetchUsers() {
        list_view = findViewById(R.id.usuario_list)
        databaseRef = FirebaseDatabase.getInstance().getReference("Users/")
        val array = ArrayList<String>()
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val nombre_usuario = ds.child("nombre_usuario").getValue(String::class.java)
                    val apellidos_usuario = ds.child("apellidos_usuario").getValue(String::class.java)
                    array.add(nombre_usuario + " " + apellidos_usuario)
                }
                val arrayAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, array)
                list_view.adapter = arrayAdapter
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