package com.example.chatapp

import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.widget.ImageButton
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.chatapp.recycleview.item.Chapter
import com.example.chatapp.recycleview.item.ChapterData
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import java.io.File
import java.io.IOException

class SeeChapters : AppCompatActivity() {
    private lateinit var crear_capitulo_btn : FloatingActionButton
    private lateinit var titulo_libro : TextView
    private var audioUri: Uri? = null
    private lateinit var databaseRef: DatabaseReference
    private lateinit var chapter_table_view : TableLayout
    private val storage = FirebaseStorage.getInstance()
    private var id_libro : String = ""
    private lateinit var returnButtonView: ImageButton
    private lateinit var sinopsis : String
    private lateinit var autor : String
    private var nombre_capitulo : String? = null
    private var audio_path : String? = null
    private var book_id : String? = null
    private var id : String? = null
    private var orden : Long? = null
    private lateinit var nombreCapituloView : TextView
    private lateinit var usuario_uuid : String
    private var path : String? = null
    private lateinit var tituloLibroView : TextView
    private lateinit var recordButton : FloatingActionButton
    private lateinit var outputFile: String
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_see_chapters)
        crear_capitulo_btn = findViewById(R.id.crear_capitulo_btn)
        mediaRecorder = MediaRecorder()
        recordButton = findViewById(R.id.recordButton)
        titulo_libro = findViewById(R.id.titulo_libro)
        titulo_libro.text = intent.getStringExtra("titulo_libro")
        val id = intent.getStringExtra("id")
        id_libro = id!!
        sinopsis = intent.getStringExtra("sinopsis").toString()
        autor = intent.getStringExtra("autor").toString()
        path = intent.getStringExtra("path")
        fetchTable()
        returnButtonView = findViewById(R.id.btnReturnBook)
        returnButtonView.setOnClickListener {
            val intent = Intent(this, WatchBook::class.java)
            intent.putExtra("id", id_libro)
            intent.putExtra("titulo", titulo_libro.text.toString())
            intent.putExtra("autor", autor)
            intent.putExtra("sinopsis", sinopsis)
            intent.putExtra("path", path)
            startActivity(intent)
        }
        tituloLibroView = findViewById(R.id.titulo_libro)
        tituloLibroView.setOnClickListener{
            if (tituloLibroView.maxLines == 1) {
                // Expand the TextView to show full text
                tituloLibroView.maxLines = Int.MAX_VALUE
                tituloLibroView.ellipsize = null
                tituloLibroView.setSingleLine(false)
            } else {
                // Collapse the TextView back to one line
                tituloLibroView.maxLines = 1
                tituloLibroView.ellipsize = TextUtils.TruncateAt.END
                tituloLibroView.setSingleLine(true)

            }
        }

        crear_capitulo_btn.setOnClickListener{
            val intent = Intent(this, AddChapter::class.java)
            intent.putExtra("id", id_libro)
            intent.putExtra("titulo", titulo_libro.text.toString())
            intent.putExtra("autor", autor)
            intent.putExtra("sinopsis", sinopsis)
            intent.putExtra("path", path)
            startActivity(intent)
        }

        recordButton.setOnClickListener {
            if (isRecording) {
                stopRecording()
                recordButton.setImageResource(R.drawable.baseline_mic_24) // Change icon back
                Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show()
            } else {
                if (checkPermissions()) {
                    startRecording()
                    recordButton.setImageResource(R.drawable.baseline_fiber_manual_record_24) // Change icon to stop
                    Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
    private fun fetchTable() {
        usuario_uuid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        databaseRef = FirebaseDatabase.getInstance().getReference("Chapters/")
        val query = databaseRef.orderByChild("book_id").equalTo(id_libro)
        chapter_table_view = findViewById(R.id.chapter_table)

        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var list_chapters = mutableListOf<ChapterData>()
                for (ds in dataSnapshot.children){
                    var nombre_capitulo = ds.child("nombre_capitulo").getValue(String::class.java)
                    var audio_path = ds.child("path").getValue(String::class.java)
                    var book_id = ds.child("book_id").getValue(String::class.java)
                    var id = ds.child("id").getValue(String::class.java)
                    var orden = ds.child("orden").getValue(Long::class.java)
                    nombreCapituloView = TextView(this@SeeChapters)
                    val deleteButton = ImageButton(this@SeeChapters)
                    val editButton = ImageButton(this@SeeChapters)
                    val row = TableRow(this@SeeChapters)
                    if(usuario_uuid != ds.child("usuario_creador").getValue(String::class.java))
                    {
                        deleteButton.isEnabled = false
                    }

                    deleteButton.setImageDrawable(ContextCompat.getDrawable(this@SeeChapters, R.drawable.baseline_delete_outline_24))
                    val selectableBackground = TypedValue()
                    theme.resolveAttribute(android.R.attr.selectableItemBackground, selectableBackground, true)
                    deleteButton.setBackgroundResource(selectableBackground.resourceId)
                    editButton.setImageDrawable(ContextCompat.getDrawable(this@SeeChapters, R.drawable.baseline_edit_24_purple))
                    editButton.setBackgroundResource(selectableBackground.resourceId)

                    deleteButton.setOnClickListener {
                        // Build the AlertDialog
                        val builder = AlertDialog.Builder(this@SeeChapters)
                        builder.setTitle("Confirmar eliminación")
                            .setMessage("¿Deseas eliminar este capítulo?")
                            .setPositiveButton("Sí") { dialog, which ->
                                if (audio_path != null)
                                {
                                    val storageRef = storage.reference

                                    val desertRef = storageRef.child(audio_path!!)
                                    // Delete the file
                                    desertRef.delete().addOnSuccessListener {
                                        databaseRef.child(id!!).removeValue()
                                        chapter_table_view.removeView(row)
                                        Toast.makeText(this@SeeChapters, "Capítulo eliminado", Toast.LENGTH_SHORT).show()
                                    }.addOnFailureListener {
                                        if (it is StorageException && it.errorCode == StorageException.ERROR_OBJECT_NOT_FOUND)
                                        {
                                            databaseRef.child(id!!).removeValue().addOnSuccessListener {
                                                chapter_table_view.removeView(row)
                                                Toast.makeText(this@SeeChapters, "Capítulo eliminado", Toast.LENGTH_SHORT).show()
                                            }.addOnFailureListener{
                                                Toast.makeText(this@SeeChapters, "Error al eliminar el capítulo", Toast.LENGTH_SHORT).show()
                                            }
                                        }else
                                        {
                                            Toast.makeText(this@SeeChapters, "Error al eliminar el capítulo", Toast.LENGTH_SHORT).show()
                                        }


                                    }
                                }

                            }
                            .setNegativeButton("No") { dialog, which ->
                                // User cancels deletion
                                dialog.dismiss()
                            }
                            .create()
                            .show()



                    }
                    editButton.setOnClickListener{
                        val intent = Intent(this@SeeChapters, AddChapter::class.java)
                        intent.putExtra("id", id)
                        intent.putExtra("titulo", nombre_capitulo)
                        intent.putExtra("titulo_libro", titulo_libro.text)
                        intent.putExtra("book_id", book_id)
                        intent.putExtra("path", audio_path)
                        intent.putExtra("orden", orden.toString())
                        intent.putExtra("autor", autor)
                        intent.putExtra("sinopsis",sinopsis)
                        intent.putExtra("usuario_creador", usuario_uuid)
                        intent.putExtra("edit", true)

                        startActivity(intent)

                    }
                    // Handling long text
                    nombreCapituloView.text = nombre_capitulo
                    nombreCapituloView.ellipsize = TextUtils.TruncateAt.END  // Truncate if text is too long
                    nombreCapituloView.maxLines = 1
                    nombreCapituloView.textSize = 20f
                    nombreCapituloView.setPadding(10, 10, 10, 10)
                    nombreCapituloView.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)

                    val autor = intent.getStringExtra("autor")
                    val sinopsis = intent.getStringExtra("sinopsis")
                    nombreCapituloView.isClickable = true
                    nombreCapituloView.setOnClickListener{
                        val intent = Intent(this@SeeChapters, PlayChapter::class.java)
                        intent.putExtra("id", id)
                        intent.putExtra("titulo", nombre_capitulo)
                        intent.putExtra("titulo_libro", titulo_libro.text.toString())
                        intent.putExtra("book_id", book_id)
                        intent.putExtra("path", audio_path)
                        intent.putExtra("orden", orden.toString())
                        intent.putExtra("autor", autor)
                        intent.putExtra("sinopsis", sinopsis)
                        intent.putExtra("path_image", path)

                        startActivity(intent)
                    }
                    row.addView(nombreCapituloView)
                    row.addView(editButton)
                    row.addView(deleteButton)

                    val ord = ds.child("orden").getValue(Long::class.java) ?: 0
                    val data = ChapterData(row, ord)
                    if (data != null) {
                        list_chapters.add(data)
                    }
                }
                for (dat in list_chapters.sortedBy { it.orden }) {
                    chapter_table_view.addView(dat.row)

                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }



        }

        query.addListenerForSingleValueEvent(eventListener)


    }



    fun startRecording() {
        if (!isRecording)
        {
            isRecording = true
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "recorded_audio_${System.currentTimeMillis()}")
                put(MediaStore.MediaColumns.MIME_TYPE, "audio/mpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MUSIC + "/BookifyRecordings")
            }
            val resolver = contentResolver
            audioUri = resolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values)

            if (audioUri != null) {
                val descriptor = resolver.openFileDescriptor(audioUri!!, "w")?.fileDescriptor
                mediaRecorder = MediaRecorder().apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                    setOutputFile(descriptor)
                    prepare()
                    start()
                }
                Log.d("Recording", "Recording started, URI: $audioUri")
            } else {
                Log.e("Recording", "Failed to create audio file in MediaStore.")
            }
        }
    }


    fun stopRecording() {
        if (isRecording)
        {
            isRecording = false
        }
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null

        if (audioUri != null) {
            Log.d("Recording", "Recording stopped. File saved at URI: $audioUri")
        } else {
            Log.e("Recording", "Recording stopped, but URI is null.")
        }
    }

    private fun checkPermissions(): Boolean {

        val permissions = arrayOf(
            android.Manifest.permission.RECORD_AUDIO)
        val missingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        return if (missingPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missingPermissions.toTypedArray(), 1)
            false
        } else {
            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

}

