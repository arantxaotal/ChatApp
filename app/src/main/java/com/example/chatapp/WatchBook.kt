package com.example.chatapp

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.w3c.dom.Text

class WatchBook(
) : AppCompatActivity() {
    private lateinit var ver_capitulos_btn : Button
    private lateinit var titulo : TextView
    private lateinit var autor : TextView
    private lateinit var sinopsis : TextView
    private lateinit var id : String
    private lateinit var returnButtonView: ImageButton
    private lateinit var imageViewP : ImageView
    private lateinit var path : String
    private lateinit var imageFileRef : StorageReference
    private lateinit var tituloTextoView : TextView
    private lateinit var autorTextView : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_watch_book)
        titulo = findViewById(R.id.titulo_texto)
        titulo.text = intent.getStringExtra("titulo")
        autor = findViewById(R.id.autor_texto)
        autor.text = intent.getStringExtra("autor")
        sinopsis = findViewById(R.id.sinopsis_texto)
        sinopsis.text = intent.getStringExtra("sinopsis")
        id = intent.getStringExtra("id").toString()
        path = intent.getStringExtra("path").toString()
        returnButtonView = findViewById(R.id.btnReturnBook)
        // Initialize Firebase Storage
        val storage = FirebaseStorage.getInstance()

        // Reference to the image in Firebase Storage
        val imageRef: StorageReference = storage.reference.child(path)

        // ImageView to display the image
        imageViewP = findViewById(R.id.imageViewPortada)

        imageRef.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(this)
                .load(uri)
                .placeholder(R.mipmap.ic_book_foreground) // Optional: Placeholder while loading
                .error(R.mipmap.ic_book_foreground) // Optional: Error placeholder
                .into(imageViewP)
        }.addOnFailureListener { exception ->
            // Handle failure
        }

        returnButtonView.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        ver_capitulos_btn = findViewById(R.id.ver_capitulos_btn)
        ver_capitulos_btn.setOnClickListener{
            val intent = Intent(this, SeeChapters::class.java)
            intent.putExtra("titulo_libro", titulo.text.toString())
            intent.putExtra("id", id)
            intent.putExtra("autor", autor.text.toString())
            intent.putExtra("sinopsis", sinopsis.text.toString())
            intent.putExtra("path", path)
            startActivity(intent)

        }
        tituloTextoView = findViewById(R.id.titulo_texto)
        tituloTextoView.setOnClickListener {
            if (tituloTextoView.maxLines == 1) {
                // Expand the TextView to show full text
                tituloTextoView.maxLines = Int.MAX_VALUE
                tituloTextoView.ellipsize = null
                tituloTextoView.setSingleLine(false)
            } else {
                // Collapse the TextView back to one line
                tituloTextoView.maxLines = 1
                tituloTextoView.ellipsize = TextUtils.TruncateAt.END
                tituloTextoView.setSingleLine(true)

            }
        }
        autorTextView = findViewById(R.id.autor_texto)
        autorTextView.setOnClickListener {
            if (autorTextView.maxLines == 1) {
                // Expand the TextView to show full text
                autorTextView.maxLines = Int.MAX_VALUE
                autorTextView.ellipsize = null
                autorTextView.setSingleLine(false)
            } else {
                // Collapse the TextView back to one line
                autorTextView.maxLines = 1
                autorTextView.ellipsize = TextUtils.TruncateAt.END
                autorTextView.setSingleLine(true)

            }
        }
    }
}