package com.example.photoeditor

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.photoeditor.model.ImageLoader
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val TAG = "Image"
    private val REQUEST_CAMERA = 10
    private val REQUEST_GALLERY = 11
    private val loader = ImageLoader()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        takePictureButton.setOnClickListener { loader.loadImage(this, "camera") }
        choosePictureButton.setOnClickListener { loader.loadImage(this, "gallery")}
        withoutPictureButton.setOnClickListener {
            val intent = Intent(this, LimitedEffectsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            val intent = Intent(this, HomeActivity::class.java)
            var uri: Uri? = null
            when (requestCode) {
                REQUEST_CAMERA -> {
                    uri = Uri.parse(loader.currentPhotoPath)
                }
                REQUEST_GALLERY -> {
                    uri = data?.data
                }
            }
            intent.putExtra(TAG, uri)
            startActivity(intent)
        }
    }
}