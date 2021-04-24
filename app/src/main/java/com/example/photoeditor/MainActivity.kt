package com.example.photoeditor

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

private lateinit var currentPhotoPath: String
private val REQUEST_CAMERA = 1
private val REQUEST_GALLERY = 2

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnTakePicture.isEnabled = false
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                111
            )
        }
        else {
            btnTakePicture.isEnabled = true
        }

        btnTakePicture.setOnClickListener(){
            takeANewPhoto()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==101){
            val pic = BitmapFactory.decodeFile(currentPhotoPath)
            imageView.setImageBitmap(pic)
            var name = "${System.currentTimeMillis()}"

        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==111 && grantResults[0]==PackageManager.PERMISSION_GRANTED){

            btnTakePicture.isEnabled = true
        }
    }
    private fun createImageFile(): File {
        val timeStamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "IMG_$timeStamp"

        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)

        currentPhotoPath = image.absolutePath

        return image
    }


    private fun takeANewPhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        try {
            val pathToImage = FileProvider.getUriForFile(this, "com.example.android.fileprovider", createImageFile())
            intent.putExtra(MediaStore.EXTRA_OUTPUT, pathToImage)
            startActivityForResult(intent, REQUEST_CAMERA)
        }

        catch (e: IOException) {
        }
    }
}