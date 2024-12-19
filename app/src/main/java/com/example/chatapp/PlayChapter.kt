package com.example.chatapp

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.recycleview.item.Chapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class PlayChapter : AppCompatActivity() {
    private lateinit var titulo_cap : TextView
    private lateinit var audioPath : TextView
    private lateinit var playButtonView: Button
    private lateinit var seekBarView: SeekBar
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var stopButtonView: Button
    private lateinit var path: String
    private var isPlaying: Boolean = false
    private val handler = Handler()
    private lateinit var titulo_path: String
    private lateinit var audioFileRef: StorageReference
    private lateinit var storageReference: StorageReference
    private lateinit var previousButtonView: Button
    private lateinit var nextButtonView: Button
    private lateinit var book_id: String
    private lateinit var orden: String
    private lateinit var previous: MutableList<Chapter>
    private lateinit var next: MutableList<Chapter>
    private lateinit var previousChapter: Chapter
    private lateinit var nextChapter: Chapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()

        previousButtonView.setOnClickListener {
            if (previous.isEmpty()) {
                Toast.makeText(this, "No hay capítulos anteriores", Toast.LENGTH_SHORT).show()
            }else
            {
                val intent = Intent(this@PlayChapter, PlayChapter::class.java)
                intent.putExtra("id", previousChapter.id)
                intent.putExtra("titulo", previousChapter.nombre_capitulo)
                intent.putExtra("book_id", previousChapter.book_id)
                intent.putExtra("path", previousChapter.path)
                intent.putExtra("orden", previousChapter.orden.toString())
                startActivity(intent)
            }



        }

        nextButtonView.setOnClickListener{
            if (next.isEmpty()) {
                Toast.makeText(this, "No hay capítulos posteriores", Toast.LENGTH_SHORT).show()
            }else
            {
                val intent = Intent(this@PlayChapter, PlayChapter::class.java)
                intent.putExtra("id", nextChapter.id)
                intent.putExtra("titulo", nextChapter.nombre_capitulo)
                intent.putExtra("book_id", nextChapter.book_id)
                intent.putExtra("path", nextChapter.path)
                intent.putExtra("orden", nextChapter.orden.toString())
                startActivity(intent)
            }

        }

        // Obtener la URL del archivo de audio
        audioFileRef.downloadUrl.addOnSuccessListener { uri ->
            // Si la URL es obtenida correctamente
            val audioUrl = uri.toString()

            // Configurar el MediaPlayer con la URL del audio
            mediaPlayer = MediaPlayer().apply {
                setDataSource(audioUrl)
                prepareAsync() // Preparar de forma asíncrona
            }

            // Configuración de la barra de progreso
            mediaPlayer?.setOnPreparedListener {
                seekBarView.max = mediaPlayer!!.duration
                startUpdatingSeekBar()
            }

            //BOTON PLAY
            playButtonView.setOnClickListener {
                mediaPlayer.start()
                if (!isPlaying) {
                    mediaPlayer?.start()
                    isPlaying = true
                    playButtonView.setBackgroundResource(R.drawable.baseline_pause_circle_24)
                    Toast.makeText(this, "Reproduciendo audio", Toast.LENGTH_SHORT).show()
                } else {
                    mediaPlayer?.pause()
                    isPlaying = false
                    playButtonView.setBackgroundResource(R.drawable.baseline_play_circle_24)
                    Toast.makeText(this, "Audio pausado", Toast.LENGTH_SHORT).show()
                }
            }


        }.addOnFailureListener { exception ->
            // Si falla al obtener la URL
            Toast.makeText(this, "Error al obtener el audio: ${exception.message}", Toast.LENGTH_LONG).show()
        }



    }
    // Función para actualizar la barra de progreso mientras se reproduce el audio
    private fun startUpdatingSeekBar() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                seekBarView.progress = mediaPlayer!!.currentPosition
                if (mediaPlayer!!.isPlaying) {
                    handler.postDelayed(this, 1000) // Actualizar cada segundo
                }
            }
        }, 1000)
    }
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release() // Libera los recursos cuando la actividad se destruye
    }
    private fun initialize() {
        setContentView(R.layout.activity_play_chapter)
        titulo_cap = findViewById(R.id.titulo_cap)
        titulo_cap.text = intent.getStringExtra("titulo")
        book_id = intent.getStringExtra("book_id").toString()
        audioPath = findViewById(R.id.audioPath)
        seekBarView = findViewById(R.id.seekBar)
        seekBarView.max = 100
        seekBarView.progress = 0
        playButtonView = findViewById(R.id.playButton)
        path = intent.getStringExtra("path").toString()
        titulo_path = intent.getStringExtra("path").toString().split("audios/")[1]
        audioPath.text = titulo_path
        // Referencia a Firebase Storage
        storageReference = FirebaseStorage.getInstance().reference
        audioFileRef = storageReference.child("${path}")
        playButtonView.setBackgroundResource(R.drawable.baseline_play_circle_24)// Ruta del archivo en Firebase Storage
        previousButtonView = findViewById(R.id.previousButton)
        nextButtonView = findViewById(R.id.nextButton)
        previousButtonView.setBackgroundResource(R.drawable.baseline_skip_previous_24)
        nextButtonView.setBackgroundResource(R.drawable.baseline_skip_next_24)
        if (intent.getStringExtra("orden") != null)
        {
            orden = intent.getStringExtra("orden").toString()
        }
        var databaseRef = FirebaseDatabase.getInstance().getReference("Chapters/")
        val query = databaseRef.orderByChild("book_id").equalTo(book_id)
        previous = mutableListOf()
        next = mutableListOf()
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (chapterSnapshot in snapshot.children) {
                    val chapter = chapterSnapshot.getValue(Chapter::class.java)
                    if (chapter != null && orden != null && orden.isNotEmpty()) {
                        // Filter users where id > age
                        if (chapter.orden < orden.toInt()) {
                            // Log or handle users that satisfy the condition
                            previous.add(chapter)
                        }
                    }
                }
                if (previous.isEmpty()) {
                    previousButtonView.isEnabled = false
                    previousButtonView.isClickable = false

                }else
                {
                    previous.sortBy { it.orden }
                    previousChapter = previous.last()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error: ${error.message}")
            }
        })


        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (chapterSnapshot in snapshot.children) {
                    val chapter = chapterSnapshot.getValue(Chapter::class.java)
                    if (chapter != null && orden != null && orden.isNotEmpty()) {
                        // Filter users where id > age
                        if (chapter.orden > orden.toInt()) {
                            // Log or handle users that satisfy the condition
                            next.add(chapter)
                        }
                    }
                }
                if (next.isEmpty()) {
                    nextButtonView.isEnabled = false
                    nextButtonView.isClickable = false
                }else
                {
                    next.sortBy { it.orden }
                    nextChapter = next.first()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error: ${error.message}")
            }
        })


    }




}