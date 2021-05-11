package com.example.photoeditor

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_segmentation.*
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier
import org.opencv.objdetect.Objdetect


class SegmentationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_segmentation)
        if (OpenCVLoader.initDebug()) {
            Log.d("myTag", "OpenCV loaded")
        }
        val receivedImage = intent.getByteArrayExtra("IMAGE")
        var bitmap: Bitmap
        if (receivedImage != null) {
            bitmap = BitmapFactory.decodeByteArray(receivedImage, 0, receivedImage.size)
            imageViewSegmentation.setImageBitmap(bitmap)
            var mat = Mat()
            var bmp32: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            Utils.bitmapToMat(bmp32, mat)
            var facesDetected = MatOfRect()
            var cascadeClassifier = CascadeClassifier()
            var minFaceSize  = Math.round(mat.rows() * 0.1f).toDouble()
            cascadeClassifier.load(".src\\main\\res\\xml\\haarcascade_frontalface_alt.xml")
            cascadeClassifier.detectMultiScale(
                mat,
                facesDetected,
                1.1,
                3,
                Objdetect.CASCADE_SCALE_IMAGE,
                Size(minFaceSize, minFaceSize),
                Size()
            )
            val facesArray: Array<Rect> = facesDetected.toArray()
            for (face in facesArray) {
                Imgproc.rectangle(mat, face.tl(), face.br(), Scalar(0.0, 0.0, 255.0), 3)
            }
        } else {
            Log.d("myTag", "OpenCV not loaded")
        }

    }
}