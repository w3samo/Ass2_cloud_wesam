package com.example.ass2_cloud_wesam

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class showfile : AppCompatActivity() {
    
    private val storage = FirebaseStorage.getInstance()
    private val list = mutableListOf<StorageReference>()
    private lateinit var progressBar: ProgressBar
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_showfile)

        FirebaseApp.initializeApp(this)
        showAllFiles()
    }
    private fun showAllFiles() {
        progressBar  = findViewById(R.id.progressBar)
        listView = findViewById(R.id.List)

        val storageRef = storage.reference
        val listAllTask = storageRef.listAll()


        progressBar.visibility = View.VISIBLE

        listAllTask.addOnSuccessListener { result ->

            list.addAll(result.items)

            val adapter = ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                list.map { it.name })

            // Set the adapter to the ListView
            listView.adapter = adapter

            listView.setOnItemClickListener { parent, view, position, id ->
                val selectedItem = parent.getItemAtPosition(position) as String
                val fileRef = storageRef.child(selectedItem)


                progressBar.visibility = View.VISIBLE

                val file = File(getExternalFilesDir(null), selectedItem)
                fileRef.getFile(file).addOnSuccessListener {

                    progressBar.visibility = View.GONE
                    Toast.makeText(this, "تم تحميل الملف بنجاح", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {

                    progressBar.visibility = View.GONE
                    Toast.makeText(this, "فشلت عملية تحميل الملف", Toast.LENGTH_SHORT).show()
                }.addOnProgressListener { taskSnapshot ->
                    val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
                    progressBar.progress = progress.toInt()
                }
            }

            progressBar.visibility = View.GONE
        }.addOnFailureListener {

        }
    }


}

