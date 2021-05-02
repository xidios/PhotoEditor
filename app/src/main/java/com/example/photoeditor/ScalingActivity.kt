package com.example.photoeditor

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import kotlinx.android.synthetic.main.activity_scaling.*
import kotlinx.android.synthetic.main.fragment_save.*

class ScalingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scaling)

        val image = intent.getParcelableExtra<Parcelable>("Image")
        imageView2.setImageURI(image as Uri)
    }
}