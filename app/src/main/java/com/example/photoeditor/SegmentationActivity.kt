package com.example.photoeditor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import org.opencv.android.OpenCVLoader

class SegmentationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_segmentation)
        if (OpenCVLoader.initDebug()) {
            Log.d("myTag", "OpenCV loaded")}
        else{
            Log.d("myTag", "OpenCV not loaded")
        }
    }
}