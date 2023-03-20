package com.example.ass2_cloud_wesam

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class MainActivity : AppCompatActivity() {

    private val storageRef = Firebase.storage.reference
    private lateinit var upload: Button
    private lateinit var down: Button

    private lateinit var progressBar: ProgressBar
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        upload = findViewById(R.id.readfile)
        down = findViewById(R.id.show)
        progressBar = findViewById(R.id.progress_bar)
        status = findViewById(R.id.status_text)
        down.setOnClickListener {
            val intent = Intent(this, showfile::class.java)
            startActivity(intent)
        }
        upload.setOnClickListener {
            selectFile()

        }

    }

    private fun selectFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/pdf"
        startActivityForResult(intent, REQUEST_CODE)
    }

    private fun uploadFile(fileUri: Uri) {
        val fileName = fileUri.lastPathSegment!!
        val fileRef = storageRef.child(fileName)

        val uploadTask = fileRef.putFile(fileUri)

        uploadTask.addOnSuccessListener {
            progressBar.visibility = View.GONE
            status.text = "تمت الاضافة بنجاح"
        }.addOnFailureListener {
            progressBar.visibility = View.GONE
            status.text = "فشلت عملية الاضافة"
        }.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
            progressBar.progress = progress.toInt()
        }
    }

    companion object {
        const val REQUEST_CODE = 101
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            data?.data?.let { fileUri ->
                progressBar.visibility = View.VISIBLE
                status.text = "جاري رفع الملف "
                uploadFile(fileUri)
            }
        }
    }

}
