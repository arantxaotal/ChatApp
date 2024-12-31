package com.example.chatapp

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
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
import androidx.core.content.res.ResourcesCompat
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.chatapp.recycleview.item.BookData
import com.example.chatapp.recycleview.item.ChapterData
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabItem
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class MainActivity : AppCompatActivity() {
    private lateinit var list_view: ListView
    private lateinit var databaseRef: DatabaseReference
    private lateinit var add_button: FloatingActionButton
    private lateinit var book_table_view : TableLayout
    private lateinit var tabs : TabLayout
    private var all : Boolean = false
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        all = intent.getBooleanExtra("all", false)
        tabs =  findViewById(R.id.tabLayout)
        fetchTable(false)

        // Handle tab selection to start a new activity
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val position = tab.position
                when (position) {
                    0 -> {
                        resetTable()
                        fetchTable(false)
                    }
                    1 -> {
                        resetTable()
                        fetchTable(true)

                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {
            }
            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })


        add_button = findViewById(R.id.floatingActionButton)
        add_button.setOnClickListener {
            val intent = Intent(this, AddBook::class.java)
            startActivity(intent)
        }




    }

    private fun fetchTable(showall : Boolean) {

        databaseRef = FirebaseDatabase.getInstance().getReference("Books/")
        val usuario_uuid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        book_table_view = findViewById(R.id.book_table)
        val query = databaseRef.orderByChild("titulo")
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var list_books = mutableListOf<BookData>()
                for (ds in dataSnapshot.children) {
                    val titulo = ds.child("titulo").getValue(String::class.java)
                    val autor = ds.child("autor").getValue(String::class.java)
                    val sinopsis = ds.child("sinopsis").getValue(String::class.java)
                    val id = ds.child("id").getValue(String::class.java)
                    val path = ds.child("path_image").getValue(String::class.java)
                    val privado = ds.child("privado").getValue(Boolean::class.java) == true
                    val usuario_uuid = ds.child("usuario_creador").getValue(String::class.java)
                    val tituloView = TextView(this@MainActivity)
                    // Initialize Firebase Storage
                    val storage = FirebaseStorage.getInstance()


                    // Reference to the image in Firebase Storage
                    val imageRef: StorageReference = storage.reference.child(path!!)

                    var imageBookView = ImageView(this@MainActivity)



                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        Glide.with(this@MainActivity)
                            .load(uri)
                            .placeholder(R.mipmap.ic_book_foreground) // Optional: Placeholder while loading
                            .error(R.mipmap.ic_book_foreground) // Optional: Error placeholder
                            .into(imageBookView)
                    }.addOnFailureListener { exception ->
                        // Handle failure
                    }

                    imageBookView.setPadding(10, 10, 10, 10)
                    imageBookView.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.5f)


                    val deleteButton = ImageButton(this@MainActivity)
                    deleteButton.setImageDrawable(ContextCompat.getDrawable(this@MainActivity, R.drawable.baseline_delete_outline_24))
                    val selectableBackground = TypedValue()
                    theme.resolveAttribute(android.R.attr.selectableItemBackground, selectableBackground, true)
                    deleteButton.setBackgroundResource(selectableBackground.resourceId)
                    val editButton = ImageButton(this@MainActivity)
                    editButton.setImageDrawable(ContextCompat.getDrawable(this@MainActivity, R.drawable.baseline_edit_24_purple))
                    editButton.setBackgroundResource(selectableBackground.resourceId)
                    editButton.setPadding(0, 10, 0, 0)
                    deleteButton.setPadding(0, 10, 0, 0)
                    val row = TableRow(this@MainActivity)

                    row.layoutParams = TableLayout.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT
                    )

                    row.setPadding(0,10,0,0)


                   if(privado)
                   {
                       // Set drawable on the left (icon resource)
                       tituloView.setCompoundDrawablesWithIntrinsicBounds(
                           0, // Left drawable
                           0, // Top drawable
                           R.drawable.baseline_lock_24, // Right drawable
                           0  // Bottom drawable
                       )

                   }else
                   {
                       // Set drawable on the left (icon resource)
                       tituloView.setCompoundDrawablesWithIntrinsicBounds(
                          0, // Left drawable
                           0, // Top drawable
                           0, // Right drawable
                           0  // Bottom drawable
                       )
                   }


                    tituloView.ellipsize = TextUtils.TruncateAt.END  // Truncate if text is too long
                    tituloView.maxLines = 2
                    tituloView.textSize = 20f
                    // Set font programmatically
                    val typeface = ResourcesCompat.getFont(this@MainActivity, R.font.almendra_sc)
                    tituloView.gravity = Gravity.CENTER_HORIZONTAL
                    tituloView.text = titulo
                    tituloView.setTypeface(typeface, Typeface.NORMAL)
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
                    imageBookView.isClickable = true
                    imageBookView.setOnClickListener {
                        val intent = Intent(this@MainActivity, WatchBook::class.java)
                        intent.putExtra("id", id)
                        intent.putExtra("titulo", titulo)
                        intent.putExtra("autor", autor)
                        intent.putExtra("sinopsis", sinopsis)
                        intent.putExtra("path", path)
                        startActivity(intent)

                    }




                    row.addView(imageBookView)
                    row.addView(tituloView)
                    row.addView(editButton)
                    row.addView(deleteButton)


                    val data = usuario_uuid?.let { BookData(row, it,privado) }
                    if (data != null) {
                        list_books.add(data)
                    }


                }

                if (showall == true)
                {
                    for (dat in list_books.filter { it.privado == false }) {
                        book_table_view.addView(dat.row)

                    }
                }else
                {
                    for (dat in list_books.filter { it.usuariouuid == usuario_uuid }) {
                        book_table_view.addView(dat.row)

                    }
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
    // Utility function to convert Bitmap to Drawable
    private fun bitmapToDrawable(context: Context, bitmap: Bitmap): BitmapDrawable {
        return BitmapDrawable(context.resources, bitmap)
    }
    private fun resetTable() {
        book_table_view.removeAllViews()
    }
}