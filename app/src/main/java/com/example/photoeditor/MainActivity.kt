package com.example.photoeditor

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import org.jetbrains.anko.toast
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.photoeditor.fragments.ImageFragment
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val IMAGE = "Image"
    private val REQUEST_CAMERA = 1
    private val REQUEST_GALLERY = 2

    private lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        takePictureButton.setOnClickListener {
            if (checkPermissions()) {
                takePhotos()
            }
            else {
                request()
            }
        }
        choosePictureButton.setOnClickListener {
            if (checkPermissions()) {
                photosFromGallery()
            }
            else {
                request()
            }
        }
    }

    private fun checkPermissions(): Boolean {
        var writeAccess = false
        var readAccess = false

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED) {
            readAccess = true
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED) {
            writeAccess = true
        }

        return (readAccess && writeAccess)
    }


    private fun request() {
        val permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        ActivityCompat.requestPermissions(this, permissions, 0)
    }


    private fun createImageFile(): File {
        // Classic title of the file with timestamp
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "IMG_$timeStamp"

        // Picking a directory to save and creating the file
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)

        // Saving a path to the file
        currentPhotoPath = image.absolutePath

        return image
    }

    private fun takePhotos() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        try {
            val pathToImage = FileProvider.getUriForFile(this, "com.example.android.fileprovider", createImageFile())
            intent.putExtra(MediaStore.EXTRA_OUTPUT, pathToImage)
            startActivityForResult(intent, REQUEST_CAMERA)
        }

        catch (e: IOException) {
            toast("Error...")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                val uri = Uri.parse(currentPhotoPath)

                val intent = Intent(this, HomeActivity::class.java).apply {
                    putExtra(IMAGE, uri)
                }
                
                startActivity(intent)
            }

            else if (requestCode == REQUEST_GALLERY) {
                val uri = data!!.data

                val intent = Intent(this, HomeActivity::class.java).apply {
                    putExtra(IMAGE, uri)
                }
                
                startActivity(intent)
            }
        }
    }

    private fun photosFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_GALLERY)
    }
}