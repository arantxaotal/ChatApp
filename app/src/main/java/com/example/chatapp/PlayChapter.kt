package com.example.chatapp

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()

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


            //BOTON STOP
            stopButtonView.setOnClickListener {
                mediaPlayer?.stop()
                mediaPlayer?.reset() // Resetear el MediaPlayer
                mediaPlayer?.setDataSource(audioUrl) // Volver a configurar la fuente
                mediaPlayer?.prepareAsync() // Preparar nuevamente
                Toast.makeText(this, "Audio detenido", Toast.LENGTH_SHORT).show()
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
        audioPath = findViewById(R.id.audioPath)
        seekBarView = findViewById(R.id.seekBar)
        seekBarView.max = 100
        seekBarView.progress = 0
        playButtonView = findViewById(R.id.playButton)
        stopButtonView = findViewById<Button>(R.id.stopButton)
        path = intent.getStringExtra("path").toString()
        titulo_path = intent.getStringExtra("path").toString().split("audios/")[1]
        audioPath.text = titulo_path
        // Referencia a Firebase Storage
        storageReference = FirebaseStorage.getInstance().reference
        audioFileRef = storageReference.child("${path}")
        playButtonView.setBackgroundResource(R.drawable.baseline_play_circle_24)// Ruta del archivo en Firebase Storage
    }




}