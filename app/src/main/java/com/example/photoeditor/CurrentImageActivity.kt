package com.example.photoeditor

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import kotlinx.android.synthetic.main.activity_current_image.*

class CurrentImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_image)
        imageView1.setImageURI(intent.getParcelableExtra<Parcelable>("Image") as Uri)
    }
}