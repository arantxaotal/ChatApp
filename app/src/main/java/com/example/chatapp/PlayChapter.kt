package com.example.chatapp

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.MediaController
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
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
    private lateinit var path: String
    private lateinit var titulo_path: String
    private lateinit var audioFileRef: StorageReference
    private lateinit var storageReference: StorageReference
    private lateinit var previousButtonView: Button
    private lateinit var nextButtonView: Button
    private lateinit var book_id: String
    private lateinit var titulo_libro: String
    private lateinit var orden: String
    private lateinit var previous: MutableList<Chapter>
    private lateinit var next: MutableList<Chapter>
    private lateinit var previousChapter: Chapter
    private lateinit var nextChapter: Chapter
    private lateinit var returnButtonView: ImageButton
    private lateinit var videoView: VideoView
    private val handler = Handler()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        titulo_cap.setOnClickListener{
            if (titulo_cap.maxLines == 1) {
                // Expand the TextView to show full text
                titulo_cap.maxLines = Int.MAX_VALUE
                titulo_cap.ellipsize = null
                titulo_cap.setSingleLine(false)
            } else {
                // Collapse the TextView back to one line
                titulo_cap.maxLines = 1
                titulo_cap.ellipsize = TextUtils.TruncateAt.END
                titulo_cap.setSingleLine(true)

            }
        }
        val autor_add = intent.getStringExtra("autor")
        val sinopsis_add = intent.getStringExtra("sinopsis")
        val path_image = intent.getStringExtra("path_image")
        val titulo_libro = intent.getStringExtra("titulo_libro")
        val book_id = intent.getStringExtra("book_id")

        returnButtonView.setOnClickListener {
            if (videoView?.isPlaying == true) {
                videoView?.stopPlayback()
            }

            val intent = Intent(this@PlayChapter, SeeChapters::class.java)
            intent.putExtra("titulo_libro", titulo_libro)
            intent.putExtra("id", book_id)
            intent.putExtra("autor", autor_add)
            intent.putExtra("sinopsis", sinopsis_add)
            intent.putExtra("autor", autor_add)
            intent.putExtra("sinopsis", sinopsis_add)
            intent.putExtra("path", path_image)

            startActivity(intent)
        }


        // Find the VideoView
        videoView= findViewById(R.id.videoView)




        previousButtonView.setOnClickListener {
            if (previous.isEmpty()) {
                Toast.makeText(this, "No hay capítulos anteriores", Toast.LENGTH_SHORT).show()
            }else
            {
                if (videoView?.isPlaying == true) {
                    videoView?.stopPlayback()
                }
                val intent = Intent(this@PlayChapter, PlayChapter::class.java)
                intent.putExtra("id", previousChapter.id)
                intent.putExtra("titulo", previousChapter.nombre_capitulo)
                intent.putExtra("book_id", previousChapter.book_id)
                intent.putExtra("titulo_libro", titulo_libro)
                intent.putExtra("path", previousChapter.path)
                intent.putExtra("orden", previousChapter.orden.toString())
                intent.putExtra("autor", autor_add)
                intent.putExtra("sinopsis", sinopsis_add)
                intent.putExtra("path_image", path_image)

                startActivity(intent)
            }



        }

        nextButtonView.setOnClickListener{
            if (next.isEmpty()) {
                Toast.makeText(this, "No hay capítulos posteriores", Toast.LENGTH_SHORT).show()
            }else
            {
                if (videoView?.isPlaying == true) {
                    videoView?.stopPlayback()
                }
                val intent = Intent(this@PlayChapter, PlayChapter::class.java)
                intent.putExtra("id", nextChapter.id)
                intent.putExtra("titulo", nextChapter.nombre_capitulo)
                intent.putExtra("titulo_libro", titulo_libro)
                intent.putExtra("book_id", nextChapter.book_id)
                intent.putExtra("path", nextChapter.path)
                intent.putExtra("orden", nextChapter.orden.toString())
                intent.putExtra("autor", autor_add)
                intent.putExtra("sinopsis", sinopsis_add)
                intent.putExtra("path_image", path_image)
                startActivity(intent)
            }

        }

        // Obtener la URL del archivo de audio
        audioFileRef.downloadUrl.addOnSuccessListener { uri ->

            // Set the video URI to the VideoView
            videoView.setVideoURI(uri)
            videoView.seekTo( 1 )



            // Set max SeekBar value to video duration
            videoView.setOnPreparedListener {
                seekBarView.max = videoView.duration
            }

            // Configuración de la barra de progreso
            videoView?.setOnPreparedListener {
                seekBarView.max = videoView!!.duration
            }

            videoView.setOnCompletionListener {
                playButtonView.setBackgroundResource(R.drawable.baseline_play_circle_24)
            }



            //BOTON PLAY
            playButtonView.setOnClickListener {
                if (!videoView.isPlaying) {
                    updateSeekBar()
                    videoView?.start()
                    playButtonView.setBackgroundResource(R.drawable.baseline_pause_circle_24)
                    Toast.makeText(this, "Reproduciendo audio", Toast.LENGTH_SHORT).show()
                } else {
                    updateSeekBar()
                    videoView?.pause()
                    playButtonView.setBackgroundResource(R.drawable.baseline_play_circle_24)
                    Toast.makeText(this, "Audio pausado", Toast.LENGTH_SHORT).show()
                }
            }
            // SeekBar change listener
            seekBarView.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        videoView.seekTo(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}

                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            })


        }.addOnFailureListener { exception ->
            // Si falla al obtener la URL
            Toast.makeText(this, "Error al obtener el audio: ${exception.message}", Toast.LENGTH_LONG).show()
        }



    }
    private fun updateSeekBar() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                seekBarView.progress = videoView.currentPosition
                handler.postDelayed(this, 1000) // Update every second
            }
        }, 0)
    }
    override fun onDestroy() {
        super.onDestroy()
        videoView.stopPlayback()
    }

    private fun initialize() {
        setContentView(R.layout.activity_play_chapter)
        returnButtonView = findViewById(R.id.btnReturn)
        titulo_cap = findViewById(R.id.titulo_cap)
        titulo_cap.text = intent.getStringExtra("titulo")
        book_id = intent.getStringExtra("book_id").toString()
        titulo_libro = intent.getStringExtra("titulo_libro").toString()
        audioPath = findViewById(R.id.audioPath)
        seekBarView = findViewById(R.id.seekBar)
        seekBarView.max = 100
        seekBarView.progress = 0
        playButtonView = findViewById(R.id.playButton)
        path = intent.getStringExtra("path").toString()
        titulo_path = intent.getStringExtra("path").toString().split("audios/")[1]
        audioPath.text = titulo_path
        audioPath.isSelected = true
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